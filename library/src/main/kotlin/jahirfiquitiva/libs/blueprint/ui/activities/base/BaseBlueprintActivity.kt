/*
 * Copyright (c) 2017. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jahirfiquitiva.libs.blueprint.ui.activities.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import ca.allanwang.kau.utils.startLink
import com.afollestad.materialdialogs.MaterialDialog
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.github.javiersantos.piracychecker.PiracyChecker
import com.github.javiersantos.piracychecker.enums.InstallerID
import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError
import com.github.javiersantos.piracychecker.enums.PirateApp
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.NavigationItem
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.frames.helpers.extensions.buildMaterialDialog
import jahirfiquitiva.libs.frames.helpers.utils.ADW_ACTION
import jahirfiquitiva.libs.frames.helpers.utils.APPLY_ACTION
import jahirfiquitiva.libs.frames.helpers.utils.ICONS_APPLIER
import jahirfiquitiva.libs.frames.helpers.utils.IMAGE_PICKER
import jahirfiquitiva.libs.frames.helpers.utils.NOVA_ACTION
import jahirfiquitiva.libs.frames.helpers.utils.PLAY_STORE_LINK_PREFIX
import jahirfiquitiva.libs.frames.helpers.utils.TURBO_ACTION
import jahirfiquitiva.libs.frames.helpers.utils.WALLS_PICKER
import jahirfiquitiva.libs.frames.providers.viewmodels.IAPItem
import jahirfiquitiva.libs.frames.providers.viewmodels.IAPViewModel
import jahirfiquitiva.libs.frames.ui.activities.base.BaseActivityWithFragments
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getStringArray
import jahirfiquitiva.libs.kauextensions.extensions.hasContent
import jahirfiquitiva.libs.kauextensions.extensions.isFirstRunEver
import jahirfiquitiva.libs.kauextensions.extensions.justUpdated
import jahirfiquitiva.libs.kauextensions.extensions.printInfo

@Suppress("LeakingThis")
abstract class BaseBlueprintActivity:BaseActivityWithFragments(), LifecycleRegistryOwner,
                                     LifecycleObserver, BillingProcessor.IBillingHandler {
    
    abstract fun getNavigationItems():Array<NavigationItem>
    
    internal var picker:Int = 0
    private var donationsReady = false
    
    override fun lightTheme():Int = R.style.BlueprintLightTheme
    override fun darkTheme():Int = R.style.BlueprintDarkTheme
    override fun amoledTheme():Int = R.style.BlueprintAmoledTheme
    override fun transparentTheme():Int = R.style.BlueprintTransparentTheme
    override fun autoStatusBarTint():Boolean = true
    
    private var checker:PiracyChecker? = null
    internal var dialog:MaterialDialog? = null
    internal var billingProcessor:BillingProcessor? = null
    
    val lcOwner = LifecycleRegistry(this)
    override fun getLifecycle():LifecycleRegistry = lcOwner
    
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        picker = getPickerKey()
        initDonations()
        startLicenseCheck()
    }
    
    internal fun initDonations() {
        if (donationsReady) return
        if (donationsEnabled) {
            if (getStringArray(R.array.donation_items).isNotEmpty()) {
                if (BillingProcessor.isIabServiceAvailable(this)) {
                    destroyBillingProcessor()
                    val licKey:String? = getLicKey()
                    if (licKey != null) {
                        billingProcessor = BillingProcessor(this, licKey, this)
                        billingProcessor?.let {
                            if (!it.isInitialized) {
                                it.initialize()
                            }
                            try {
                                donationsEnabled = it.isOneTimePurchaseSupported
                            } catch (ignored:Exception) {
                            }
                            donationsReady = true
                        }
                    } else {
                        donationsEnabled = false
                    }
                } else {
                    donationsEnabled = false
                }
            } else {
                donationsEnabled = false
            }
        }
    }
    
    private fun startLicenseCheck() {
        if (isFirstRunEver || justUpdated || (!bpKonfigs.functionalDashboard)) {
            checker = getLicenseChecker()
            if (checker != null) {
                checker?.let {
                    with(it) {
                        callback(object:PiracyCheckerCallback() {
                            override fun allow() = showLicensedDialog()
                            
                            override fun dontAllow(error:PiracyCheckerError, app:PirateApp?) =
                                    showNotLicensedDialog(app)
                            
                            override fun onError(error:PiracyCheckerError) {
                                super.onError(error)
                                showLicenseErrorDialog()
                            }
                        })
                        start()
                    }
                }
            } else {
                printInfo("License checker was null. Enabling dashboard features.")
                bpKonfigs.functionalDashboard = true
            }
        }
    }
    
    internal fun getShortcut():String {
        if (intent != null && intent.dataString != null && intent.dataString.contains(
                "_shortcut")) {
            return intent.dataString
        }
        return ""
    }
    
    internal fun getPickerKey():Int {
        if (intent != null && intent.action != null) {
            return when (intent.action) {
                APPLY_ACTION -> ICONS_APPLIER
                ADW_ACTION, TURBO_ACTION, NOVA_ACTION, Intent.ACTION_PICK, Intent.ACTION_GET_CONTENT -> IMAGE_PICKER
                Intent.ACTION_SET_WALLPAPER -> WALLS_PICKER
                else -> 0
            }
        }
        return 0
    }
    
    open var donationsEnabled = false
    open fun amazonInstallsEnabled():Boolean = false
    open fun checkLPF():Boolean = true
    open fun checkStores():Boolean = true
    abstract fun getLicKey():String?
    
    // Not really needed to override
    open fun getLicenseChecker():PiracyChecker? {
        destroyChecker() // Important
        val checker = PiracyChecker(this)
        getLicKey()?.let {
            if (it.hasContent() && it.length > 50) checker.enableGooglePlayLicensing(it)
        }
        checker.enableInstallerId(InstallerID.GOOGLE_PLAY)
        if (amazonInstallsEnabled()) checker.enableInstallerId(InstallerID.AMAZON_APP_STORE)
        if (checkLPF()) checker.enableUnauthorizedAppsCheck()
        if (checkStores()) checker.enableStoresCheck()
        checker.enableEmulatorCheck(true).enableDebugCheck()
        return checker
    }
    
    internal fun showLicensedDialog() {
        destroyDialog()
        dialog = buildMaterialDialog {
            title(R.string.license_valid_title)
            content(getString(R.string.license_valid_content, getAppName()))
            positiveText(android.R.string.ok)
            onPositive { _, _ -> bpKonfigs.functionalDashboard = true }
        }
        dialog?.setOnDismissListener { bpKonfigs.functionalDashboard = true }
        dialog?.setOnCancelListener { bpKonfigs.functionalDashboard = true }
        dialog?.show()
    }
    
    internal fun showNotLicensedDialog(pirateApp:PirateApp?) {
        destroyDialog()
        val pirateAppName = pirateApp?.name ?: ""
        val content = if (pirateAppName.hasContent()) {
            getString(R.string.license_invalid_content, getAppName(),
                      getString(R.string.license_invalid_content_extra, pirateAppName))
        } else {
            getString(R.string.license_invalid_content, getAppName())
        }
        dialog = buildMaterialDialog {
            title(R.string.license_invalid_title)
            content(content)
            positiveText(android.R.string.ok)
            neutralText(R.string.download)
            onPositive { _, _ ->
                bpKonfigs.functionalDashboard = false
                finish()
            }
            onNeutral { _, _ ->
                bpKonfigs.functionalDashboard = false
                startLink(PLAY_STORE_LINK_PREFIX + packageName)
                finish()
            }
        }
        dialog?.setOnDismissListener {
            bpKonfigs.functionalDashboard = false
            finish()
        }
        dialog?.setOnCancelListener {
            bpKonfigs.functionalDashboard = false
            finish()
        }
        dialog?.show()
    }
    
    internal fun showLicenseErrorDialog() {
        destroyDialog()
        dialog = buildMaterialDialog {
            title(R.string.error_title)
            content(R.string.license_error_content)
            positiveText(android.R.string.ok)
            neutralText(R.string.try_now)
            onPositive { _, _ ->
                bpKonfigs.functionalDashboard = false
                finish()
            }
            onNeutral { _, _ ->
                bpKonfigs.functionalDashboard = false
                startLicenseCheck()
            }
        }
        dialog?.setOnDismissListener {
            bpKonfigs.functionalDashboard = false
            finish()
        }
        dialog?.setOnCancelListener {
            bpKonfigs.functionalDashboard = false
            finish()
        }
        dialog?.show()
    }
    
    internal fun doDonation() {
        initDonations()
        destroyDialog()
        if (!donationsReady) {
            showDonationErrorDialog(0, null)
            return
        }
        billingProcessor?.let {
            if (!it.isInitialized) {
                it.initialize()
            }
            if (it.isInitialized) {
                val donationViewModel = ViewModelProviders.of(this).get(IAPViewModel::class.java)
                donationViewModel.iapBillingProcessor = it
                donationViewModel.items.observe(this, Observer<ArrayList<IAPItem>> { list ->
                    if (list != null) {
                        if (list.size > 0) {
                            showDonationDialog(list)
                        } else {
                            showDonationErrorDialog(0, null)
                        }
                    } else {
                        showDonationErrorDialog(0, null)
                    }
                    donationViewModel.items.removeObservers(this)
                })
                destroyDialog()
                dialog = buildMaterialDialog {
                    content(R.string.loading)
                    progress(true, 0)
                    cancelable(false)
                }
                donationViewModel.loadData(getStringArray(R.array.donation_items), true)
                dialog?.show()
            }
        }
    }
    
    private fun showDonationDialog(items:ArrayList<IAPItem>) {
        destroyDialog()
        dialog = buildMaterialDialog {
            title(R.string.donate)
            items(items)
            itemsCallbackSingleChoice(0, { _, _, which, _ ->
                billingProcessor?.purchase(this@BaseBlueprintActivity, items[which].id)
                true
            })
            negativeText(android.R.string.cancel)
            positiveText(R.string.donate)
        }
        dialog?.show()
    }
    
    private fun showDonationErrorDialog(error:Int, reason:String?) {
        destroyDialog()
        dialog = buildMaterialDialog {
            title(R.string.error_title)
            content(getString(R.string.donate_error, error.toString(),
                              reason ?: getString(R.string.donate_error_unknown)))
        }
        dialog?.show()
    }
    
    override fun onProductPurchased(productId:String, details:TransactionDetails?) {
        billingProcessor?.let {
            if (it.consumePurchase(productId)) {
                destroyDialog()
                dialog = buildMaterialDialog {
                    title(R.string.donate_success_title)
                    content(getString(R.string.donate_success_content, getAppName()))
                    positiveText(R.string.close)
                }
                dialog?.show()
            }
        }
    }
    
    override fun onBillingError(errorCode:Int, error:Throwable?) {
        showDonationErrorDialog(errorCode,
                                (error?.message ?: getString(R.string.donate_error_unknown)))
        destroyBillingProcessor()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun onDestroy() {
        super.onDestroy()
        destroyDialog()
        destroyBillingProcessor()
        destroyChecker()
    }
    
    fun destroyChecker() {
        checker?.destroy()
        checker = null
    }
    
    fun destroyDialog() {
        dialog?.dismiss()
        dialog = null
    }
    
    fun destroyBillingProcessor() {
        billingProcessor?.release()
        billingProcessor = null
        donationsReady = false
    }
    
    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        if (billingProcessor != null) {
            billingProcessor?.let {
                if (!(it.handleActivityResult(requestCode, resultCode, data))) {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    
    override fun onBillingInitialized() {}
    
    override fun onPurchaseHistoryRestored() {}
}