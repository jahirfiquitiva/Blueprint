/*
 * Copyright (c) 2018. Jahir Fiquitiva
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
package jahirfiquitiva.libs.blueprint.helpers.extensions

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import ca.allanwang.kau.utils.openLink
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.BL
import jahirfiquitiva.libs.blueprint.models.Launcher
import jahirfiquitiva.libs.frames.helpers.extensions.jfilter
import jahirfiquitiva.libs.frames.helpers.utils.PLAY_STORE_LINK_PREFIX
import jahirfiquitiva.libs.kext.extensions.color
import jahirfiquitiva.libs.kext.extensions.mdDialog
import jahirfiquitiva.libs.kext.extensions.stringArray
import jahirfiquitiva.libs.kuper.helpers.extensions.isAppInstalled

internal val Context.supportedLaunchers: ArrayList<Launcher>
    get() = arrayListOf(
        Launcher(
            "action",
            "Action Launcher", arrayOf("com.actionlauncher.playstore"),
            color(R.color.action_launcher_color)),
        Launcher(
            "adw",
            "ADW Launcher", arrayOf("org.adw.launcher"),
            color(R.color.adw_launcher_color)),
        Launcher(
            "adwex",
            "ADW Ex Launcher", arrayOf("org.adwfreak.launcher"),
            color(R.color.adw_ex_launcher_color)),
        Launcher(
            "apex",
            "Apex Launcher", arrayOf("com.anddoes.launcher"),
            color(R.color.apex_launcher_color)),
        /*
        Launcher(
            "atom",
            "Atom Launcher", arrayOf("com.dlto.atom.launcher"),
            color(R.color.atom_launcher_color)),
        Launcher(
            "aviate",
            "Aviate Launcher", arrayOf("com.tul.aviate"),
            color(R.color.aviate_launcher_color)),
            */
        Launcher(
            "go",
            "Go Launcher", arrayOf("com.gau.go.launcherex"),
            color(R.color.go_launcher_color)),
        Launcher(
            "googlenow",
            "Google Now Launcher", arrayOf("com.google.android.launcher"),
            color(R.color.google_now_launcher_color), false),
        Launcher(
            "holo",
            "Holo Launcher", arrayOf("com.mobint.hololauncher"),
            color(R.color.holo_launcher_color)),
        Launcher(
            "holoics",
            "Holo Launcher ICS", arrayOf("com.mobint.hololauncher.hd"),
            color(R.color.holo_ics_launcher_color)),
        /*
        Launcher(
            "kk",
            "KK Launcher", arrayOf("com.kk.launcher"),
            color(R.color.kk_launcher_color)),
            */
        Launcher(
            "lg",
            "LG Home", arrayOf("com.lge.launcher2"),
            color(R.color.lg_home_color)),
        /*
        Launcher(
            "l",
            "L Launcher", arrayOf("com.l.launcher"),
            color(R.color.l_launcher_color)),
            */
        Launcher(
            "lawnchair",
            "Lawnchair", arrayOf(
            "ch.deletescape.lawnchair.plah", "ch.deletescape.lawnchair",
            "ch.deletescape.lawnchair.ci", "ch.deletescape.lawnchair.dev"),
            color(R.color.lawnchair_launcher_color)),
        Launcher(
            "lineageos", "LineageOS Theme Engine",
            arrayOf(
                "org.cyanogenmod.theme.chooser", "org.cyanogenmod.theme.chooser2",
                "com.cyngn.theme.chooser"),
            color(R.color.cm_theme_engine_color)),
        Launcher(
            "lucid",
            "Lucid Launcher", arrayOf("com.powerpoint45.launcher"),
            color(R.color.lucid_launcher_color)),
        /*
        Launcher(
            "mini",
            "Mini Launcher", arrayOf("com.jiubang.go.mini.launcher"),
            color(R.color.mini_launcher_color)),
        Launcher(
            "next",
            "Next Launcher", arrayOf("com.gtp.nextlauncher"),
            color(R.color.next_launcher_color)),
            */
        Launcher(
            "nova",
            "Nova Launcher", arrayOf("com.teslacoilsw.launcher"),
            color(R.color.nova_launcher_color)),
        Launcher(
            "pixel",
            "Pixel Launcher", arrayOf("com.google.android.apps.nexuslauncher"),
            color(R.color.pixel_launcher_color), false),
        /*
        Launcher(
            "s",
            "S Launcher", arrayOf("com.galaxy.s.launcher"),
            color(R.color.s_launcher_color)),
            */
        Launcher(
            "smart",
            "Smart Launcher", arrayOf("ginlemon.flowerfree"),
            color(R.color.smart_launcher_color)),
        Launcher(
            "smartpro",
            "Smart Launcher Pro", arrayOf("ginlemon.flowerpro"),
            color(R.color.smart_pro_launcher_color)),
        Launcher(
            "solo",
            "Solo Launcher", arrayOf("home.solo.launcher.free"),
            color(R.color.solo_launcher_color)),
        Launcher(
            "tsf",
            "TSF Launcher", arrayOf("com.tsf.shell"),
            color(R.color.tsf_launcher_color))
        /*
        Launcher(
            "unicon",
            "Unicon", arrayOf("sg.ruqqq.IconThemer"),
            color(R.color.unicon_pro_color))*/
                       )

internal val Context.enabledLaunchers: ArrayList<Launcher>
    get() {
        val enabled = stringArray(R.array.launchers).orEmpty()
        return supportedLaunchers.jfilter { enabled.contains(it.key) }
    }

fun Context.executeLauncherIntent(launcherKey: String) {
    if (launcherKey.isEmpty()) return
    for ((index, item) in supportedLaunchers.withIndex()) {
        if (item.name.equals(launcherKey, true) || item.hasPackage(launcherKey)) {
            when (index) {
                0 -> executeActionLauncherIntent()
                1 -> executeAdwLauncherIntent()
                2 -> executeAdwEXLauncherIntent()
                3 -> executeApexLauncherIntent()
                // 4 -> executeAtomLauncherIntent()
                // 5 -> executeAviateLauncherIntent()
                4 -> executeGoLauncherIntent()
                5, 13 -> executeIconPacksNotSupportedIntent()
                6 -> executeHoloLauncherIntent()
                7 -> executeHoloLauncherICSIntent()
                // 8 -> executeKkLauncherIntent()
                8 -> executeLgHomeLauncherIntent()
                // 10 -> executeLLauncherIntent()
                9 -> executeLawnchairIntent()
                10 -> executeLineageOSThemeEngineIntent()
                11 -> executeLucidLauncherIntent()
                // 14 -> executeMiniLauncherIntent()
                // 15 -> executeNextLauncherIntent()
                12 -> executeNovaLauncherIntent()
                // 14 -> executeSLauncherIntent()
                14 -> executeSmartLauncherIntent()
                15 -> executeSmartLauncherProIntent()
                16 -> executeSoloLauncherIntent()
                17 -> executeTsfLauncherIntent()
                // 18 -> executeUniconIntent()
                else -> showLauncherApplyError()
            }
        }
    }
}

private fun Context.executeIconPacksNotSupportedIntent() {
    try {
        mdDialog {
            title(R.string.no_compatible_launcher_title)
            message(R.string.no_compatible_launcher_content)
            positiveButton(android.R.string.ok) {
                openLink(PLAY_STORE_LINK_PREFIX + "com.momocode.shortcuts")
            }
            negativeButton(android.R.string.cancel)
        }.show()
    } catch (e: Exception) {
        BL.e(e.message, e)
    }
}

internal fun Context.showLauncherNotInstalledDialog(item: Launcher) {
    try {
        mdDialog {
            title(text = item.name)
            message(text = getString(R.string.lni_content, item.name))
            positiveButton(android.R.string.ok) {
                openLink(PLAY_STORE_LINK_PREFIX + item.packageNames[0])
            }
            negativeButton(android.R.string.cancel)
        }.show()
    } catch (e: Exception) {
        BL.e(e.message, e)
    }
}

internal fun Context.showLauncherApplyError(customContent: String? = null) {
    try {
        mdDialog {
            title(R.string.error_title)
            message(text = customContent ?: getString(R.string.coming_soon))
            positiveButton(android.R.string.ok)
        }.show()
    } catch (e: Exception) {
        BL.e(e.message, e)
    }
}

private fun Context.attemptApply(customContent: String? = null, intent: () -> Intent?) {
    try {
        intent()?.let { startActivity(it) } ?: {
            BL.e("Intent was null!")
            showLauncherApplyError(customContent)
        }()
    } catch (e: Exception) {
        BL.e(e.message, e)
        showLauncherApplyError(customContent)
    }
}

private fun Context.executeActionLauncherIntent() {
    attemptApply {
        packageManager
            .getLaunchIntentForPackage("com.actionlauncher.playstore")?.apply {
                putExtra("apply_icon_pack", packageName)
            }
    }
}

private fun Context.executeAdwLauncherIntent() {
    attemptApply {
        Intent("org.adw.launcher.SET_THEME").apply {
            putExtra("org.adw.launcher.theme.NAME", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

private fun Context.executeAdwEXLauncherIntent() {
    attemptApply {
        Intent("org.adwfreak.launcher.SET_THEME").apply {
            putExtra("org.adwfreak.launcher.theme.NAME", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

private fun Context.executeApexLauncherIntent() {
    attemptApply {
        Intent("com.anddoes.launcher.SET_THEME").apply {
            putExtra("com.anddoes.launcher.THEME_PACKAGE_NAME", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

private fun Context.executeAtomLauncherIntent() {
    attemptApply {
        Intent("com.dlto.atom.launcher.action.ACTION_VIEW_THEME_SETTINGS").apply {
            `package` = "com.dlto.atom.launcher"
            putExtra("packageName", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

private fun Context.executeAviateLauncherIntent() {
    attemptApply {
        Intent("com.tuaviate.SET_THEME").apply {
            `package` = "com.tuaviate"
            putExtra("THEME_PACKAGE", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

private fun Context.executeGoLauncherIntent() {
    attemptApply {
        packageManager.getLaunchIntentForPackage("com.gau.go.launcherex").also {
            val go = Intent("com.gau.go.launcherex.MyThemes.mythemeaction")
            go.putExtra("type", 1)
            go.putExtra("pkgname", packageName)
            sendBroadcast(go)
        }
    }
}

private fun Context.executeHoloLauncherIntent() {
    attemptApply {
        Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName("com.mobint.hololauncher", "com.mobint.hololauncher.Settings")
        }
    }
}

private fun Context.executeHoloLauncherICSIntent() {
    attemptApply {
        Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName(
                "com.mobint.hololauncher.hd", "com.mobint.hololauncher.SettingsActivity")
        }
    }
}

private fun Context.executeKkLauncherIntent() {
    attemptApply {
        Intent("com.kk.launcher.APPLY_ICON_THEME").apply {
            putExtra("com.kk.launcher.theme.EXTRA_PKG", packageName)
            putExtra("com.kk.launcher.theme.EXTRA_NAME", getString(R.string.app_name))
        }
    }
}

private fun Context.executeLgHomeLauncherIntent() {
    attemptApply {
        Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName(
                "com.lge.launcher2",
                "com.lge.launcher2.homesettings.HomeSettingsPrefActivity")
        }
    }
}

private fun Context.executeLLauncherIntent() {
    attemptApply {
        Intent("com.launcher.APPLY_ICON_THEME", null).apply {
            putExtra("com.launcher.theme.EXTRA_PKG", packageName)
        }
    }
}

private fun Context.executeLawnchairIntent() {
    attemptApply {
        Intent("ch.deletescape.lawnchair.APPLY_ICONS", null).apply {
            putExtra("packageName", packageName)
        }
    }
}

private fun Context.executeLineageOSThemeEngineIntent() {
    var themesAppInstalled = isAppInstalled("org.cyanogenmod.theme.chooser") ||
        isAppInstalled("org.cyanogenmod.theme.chooser2") ||
        isAppInstalled("com.cyngn.theme.chooser")
    
    attemptApply(
        if (themesAppInstalled) getString(R.string.impossible_open_themes)
        else getString(R.string.themes_app_not_installed)) {
        Intent("android.action.MAIN").apply {
            when {
                isAppInstalled("org.cyanogenmod.theme.chooser") -> {
                    component = ComponentName(
                        "org.cyanogenmod.theme.chooser",
                        "org.cyanogenmod.theme.chooser.ChooserActivity")
                }
                isAppInstalled("org.cyanogenmod.theme.chooser2") -> {
                    component = ComponentName(
                        "org.cyanogenmod.theme.chooser2",
                        "org.cyanogenmod.theme.chooser2.ChooserActivity")
                }
                isAppInstalled("com.cyngn.theme.chooser") -> {
                    component = ComponentName(
                        "com.cyngn.theme.chooser",
                        "com.cyngn.theme.chooser.ChooserActivity")
                }
                else -> themesAppInstalled = false
            }
            if (themesAppInstalled) putExtra("pkgName", packageName)
        }
    }
}

private fun Context.executeLucidLauncherIntent() {
    attemptApply {
        Intent("com.powerpoint45.action.APPLY_THEME", null).apply {
            putExtra("icontheme", packageName)
        }
    }
}

private fun Context.executeMiniLauncherIntent() {
    attemptApply {
        Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName(
                "com.jiubang.go.mini.launcher",
                "com.jiubang.go.mini.launcher.setting.MiniLauncherSettingActivity")
        }
    }
}

private fun Context.executeNextLauncherIntent() {
    attemptApply {
        var nextApply: Intent? = packageManager.getLaunchIntentForPackage("com.gtp.nextlauncher")
        if (nextApply == null) {
            nextApply = packageManager.getLaunchIntentForPackage("com.gtp.nextlauncher.trial")
        }
        val next = Intent("com.gau.go.launcherex.MyThemes.mythemeaction")
        next.putExtra("type", 1)
        next.putExtra("pkgname", packageName)
        sendBroadcast(next)
        nextApply
    }
}

private fun Context.executeNovaLauncherIntent() {
    attemptApply {
        Intent("com.teslacoilsw.launcher.APPLY_ICON_THEME").apply {
            `package` = "com.teslacoilsw.launcher"
            putExtra("com.teslacoilsw.launcher.extra.ICON_THEME_TYPE", "GO")
            putExtra("com.teslacoilsw.launcher.extra.ICON_THEME_PACKAGE", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

private fun Context.executeSLauncherIntent() {
    attemptApply {
        Intent("com.s.launcher.APPLY_ICON_THEME").apply {
            putExtra("com.s.launcher.theme.EXTRA_PKG", packageName)
            putExtra("com.s.launcher.theme.EXTRA_NAME", getString(R.string.app_name))
        }
    }
}

private fun Context.executeSmartLauncherIntent() {
    attemptApply {
        Intent("ginlemon.smartlauncher.setGSLTHEME").apply {
            putExtra("package", packageName)
        }
    }
}

private fun Context.executeSmartLauncherProIntent() {
    attemptApply {
        Intent("ginlemon.smartlauncher.setGSLTHEME").apply {
            putExtra("package", packageName)
        }
    }
}

private fun Context.executeSoloLauncherIntent() {
    attemptApply {
        packageManager.getLaunchIntentForPackage("home.solo.launcher.free").also {
            val solo = Intent("home.solo.launcher.free.APPLY_THEME")
            solo.putExtra("EXTRA_PACKAGENAME", packageName)
            solo.putExtra("EXTRA_THEMENAME", getString(R.string.app_name))
            sendBroadcast(solo)
        }
    }
}

private fun Context.executeTsfLauncherIntent() {
    attemptApply {
        packageManager.getLaunchIntentForPackage("com.tsf.shell").also {
            val tsf = Intent("android.action.MAIN")
            tsf.component = ComponentName("com.tsf.shell", "com.tsf.shelShellActivity")
            sendBroadcast(tsf)
        }
    }
}

private fun Context.executeUniconIntent() {
    attemptApply {
        Intent("android.action.MAIN").apply {
            addCategory("android.category.LAUNCHER")
            `package` = "sg.ruqqq.IconThemer"
        }
    }
}

internal val Context.defaultLauncher: Launcher?
    get() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            val resolveInfo =
                packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            val launcherPackage = resolveInfo?.activityInfo?.packageName
            supportedLaunchers.forEach { if (it.hasPackage(launcherPackage.toString())) return it }
            return null
        } catch (e: Exception) {
            return null
        }
    }
