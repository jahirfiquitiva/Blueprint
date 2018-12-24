package jahirfiquitiva.libs.blueprint.helpers.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import androidx.annotation.StringRes
import ca.allanwang.kau.utils.toast
import jahirfiquitiva.libs.blueprint.BuildConfig
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.BL
import jahirfiquitiva.libs.kext.extensions.getAppVersion
import jahirfiquitiva.libs.kext.extensions.getAppVersionCode
import jahirfiquitiva.libs.kext.extensions.string
import jahirfiquitiva.libs.kuper.helpers.extensions.isAppInstalled

/**
 * Created by Allan Wang on 2017-06-20.
 *
 * Helper tool to call an email intent with device information
 */
private class EmailBuilder(val email: String, val subject: String) {
    var message: String = "Write here."
    var deviceDetails: Boolean = true
    var appInfo: Boolean = true
    var footer: String? = null
    private val pairs: MutableMap<String, String> = mutableMapOf()
    private val packages: MutableList<Package> = mutableListOf()
    private var attachment: Uri? = null
    
    fun checkPackage(packageName: String, appName: String) =
        packages.add(Package(packageName, appName))
    
    fun addItem(key: String, value: String) = pairs.put(key, value)
    
    fun addAttachment(uri: Uri) {
        attachment = uri
    }
    
    /**
     * Optional handler to update the created intent
     */
    var extras: Intent.() -> Unit = {}
    
    data class Package(val packageName: String, val appName: String)
    
    @SuppressLint("NewApi")
    fun getIntent(context: Context): Intent {
        val intent = Intent(Intent.ACTION_SEND)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            .putExtra(Intent.EXTRA_SUBJECT, subject)
        val emailBuilder = StringBuilder()
        emailBuilder.append(message).append("\n\n")
        if (deviceDetails) {
            val installer =
                context.packageManager.getInstallerPackageName(context.packageName) ?: "None"
            
            val deviceItems = mutableMapOf(
                "OS Version" to "${System.getProperty(
                    "os.version")} (${Build.VERSION.INCREMENTAL})",
                "OS API Level" to Build.VERSION.SDK_INT,
                "Device (Manufacturer)" to "${Build.DEVICE} (${Build.MANUFACTURER})",
                "Model (Product)" to "${Build.MODEL} (${Build.PRODUCT})",
                "Blueprint Version" to BuildConfig.LIB_VERSION,
                "App Version" to "${context.getAppVersionCode()} (${context.getAppVersion()}) from $installer"
                                          )
            if (context is Activity) {
                val metric = DisplayMetrics()
                context.windowManager.defaultDisplay.getMetrics(metric)
                deviceItems["Screen Dimensions"] = "${metric.widthPixels} x ${metric.heightPixels}"
            }
            deviceItems.forEach { (k, v) -> emailBuilder.append("$k: $v\n") }
        }
        if (appInfo) {
            try {
                val appInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                
                @Suppress("DEPRECATION")
                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    appInfo.longVersionCode.toString()
                } else appInfo.versionCode.toString()
                
                emailBuilder.append("\nApp: ").append(context.packageName)
                    .append("\nApp Version Name: ").append(appInfo.versionName)
                    .append("\nApp Version Code: ").append(versionCode).append("\n")
            } catch (e: PackageManager.NameNotFoundException) {
                BL.e("EmailBuilder packageInfo not found")
            }
        }
        
        if (packages.isNotEmpty()) {
            emailBuilder.append("\n")
            packages.forEach {
                if (context.isAppInstalled(it.packageName))
                    emailBuilder.append(String.format("\n%s is installed", it.appName))
            }
        }
        
        if (pairs.isNotEmpty()) {
            emailBuilder.append("\n")
            pairs.forEach { (k, v) -> emailBuilder.append("$k: $v\n") }
        }
        
        if (footer != null)
            emailBuilder.append("\n").append(footer)
        
        intent.putExtra(Intent.EXTRA_TEXT, emailBuilder.toString())
        intent.type = "text/plain"
        return intent
    }
    
    /**
     * Create the intent and send the request when possible
     * If a stream uri is added, it will automatically be flagged to pass on read permissions
     */
    fun execute(context: Context) {
        val intent = getIntent(context)
        intent.extras()
        val packageName = intent.resolveActivity(context.packageManager)?.packageName
        packageName?.let {
            val attachment = this.attachment
            if (attachment != null) {
                context.grantUriPermission(
                    packageName, attachment, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(Intent.EXTRA_STREAM, attachment)
            }
            context.startActivity(Intent.createChooser(intent, context.string(R.string.send_using)))
        } ?: { context.toast(R.string.no_mail) }()
    }
}

fun Context.sendEmail(
    @StringRes emailId: Int,
    @StringRes subjectId: Int
                     ) =
    sendEmail(string(emailId), string(subjectId))

fun Context.sendEmail(email: String, subject: String) {
    EmailBuilder(email, subject).apply {
        execute(this@sendEmail)
    }
}