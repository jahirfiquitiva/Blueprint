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

package jahirfiquitiva.libs.blueprint.extensions

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import ca.allanwang.kau.utils.isAppInstalled
import ca.allanwang.kau.utils.materialDialog
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.models.Launcher
import jahirfiquitiva.libs.frames.utils.PLAY_STORE_LINK_PREFIX
import jahirfiquitiva.libs.kauextensions.extensions.getColorFromRes
import jahirfiquitiva.libs.kauextensions.extensions.openLink
import jahirfiquitiva.libs.kauextensions.extensions.showToast

val Context.supportedLaunchers:Array<Launcher>
    get() = arrayOf(
            Launcher("Action Launcher", arrayOf("com.actionlauncher.playstore"),
                     getColorFromRes(R.color.action_launcher_color)),
            Launcher("ADW Launcher", arrayOf("org.adw.launcher"),
                     getColorFromRes(R.color.adw_launcher_color)),
            Launcher("ADW Ex Launcher", arrayOf("org.adwfreak.launcher"),
                     getColorFromRes(R.color.adw_ex_launcher_color)),
            Launcher("Apex Launcher", arrayOf("com.anddoes.launcher"),
                     getColorFromRes(R.color.apex_launcher_color)),
            Launcher("Atom Launcher", arrayOf("com.dlto.atom.launcher"),
                     getColorFromRes(R.color.atom_launcher_color)),
            Launcher("Aviate Launcher", arrayOf("com.tul.aviate"),
                     getColorFromRes(R.color.aviate_launcher_color)),
            Launcher("LineageOS Theme Engine",
                     arrayOf("org.cyanogenmod.theme.chooser", "org.cyanogenmod.theme.chooser2",
                             "com.cyngn.theme.chooser"),
                     getColorFromRes(R.color.cm_theme_engine_color)),
            Launcher("Go Launcher", arrayOf("com.gau.go.launcherex"),
                     getColorFromRes(R.color.go_launcher_color)),
            Launcher("Google Now Launcher", arrayOf("com.google.android.launcher"),
                     getColorFromRes(R.color.google_now_launcher_color), false),
            Launcher("Holo Launcher", arrayOf("com.mobint.hololauncher"),
                     getColorFromRes(R.color.holo_launcher_color)),
            Launcher("Holo Launcher ICS", arrayOf("com.mobint.hololauncher.hd"),
                     getColorFromRes(R.color.holo_ics_launcher_color)),
            Launcher("KK Launcher", arrayOf("com.kk.launcher"),
                     getColorFromRes(R.color.kk_launcher_color)),
            Launcher("LG Home", arrayOf("com.lge.launcher2"),
                     getColorFromRes(R.color.lg_home_color)),
            Launcher("L Launcher", arrayOf("com.l.launcher"),
                     getColorFromRes(R.color.l_launcher_color)),
            Launcher("Lucid Launcher", arrayOf("com.powerpoint45.launcher"),
                     getColorFromRes(R.color.lucid_launcher_color)),
            Launcher("Mini Launcher", arrayOf("com.jiubang.go.mini.launcher"),
                     getColorFromRes(R.color.mini_launcher_color)),
            Launcher("Next Launcher", arrayOf("com.gtp.nextlauncher"),
                     getColorFromRes(R.color.next_launcher_color)),
            Launcher("Nova Launcher", arrayOf("com.teslacoilsw.launcher"),
                     getColorFromRes(R.color.nova_launcher_color)),
            Launcher("Pixel Launcher", arrayOf("com.google.android.apps.nexuslauncher"),
                     getColorFromRes(R.color.pixel_launcher_color), false),
            Launcher("S Launcher", arrayOf("com.s.launcher"),
                     getColorFromRes(R.color.s_launcher_color)),
            Launcher("Smart Launcher", arrayOf("ginlemon.flowerfree"),
                     getColorFromRes(R.color.smart_launcher_color)),
            Launcher("Smart Launcher Pro", arrayOf("ginlemon.flowerpro"),
                     getColorFromRes(R.color.smart_pro_launcher_color)),
            Launcher("Solo Launcher", arrayOf("home.solo.launcher.free"),
                     getColorFromRes(R.color.solo_launcher_color)),
            Launcher("TSF Launcher", arrayOf("com.tsf.shell"),
                     getColorFromRes(R.color.tsf_launcher_color)),
            Launcher("Unicon", arrayOf("sg.ruqqq.IconThemer"),
                     getColorFromRes(R.color.unicon_pro_color))
                   )

fun Context.executeLauncherIntent(launcherKey:String) {
    if (launcherKey.isEmpty()) return
    for ((index, item) in supportedLaunchers.withIndex()) {
        if (item.name.equals(launcherKey, true) || item.hasPackage(launcherKey)) {
            when (index) {
                0 -> executeActionLauncherIntent()
                1 -> executeAdwLauncherIntent()
                2 -> executeAdwEXLauncherIntent()
                3 -> executeApexLauncherIntent()
                4 -> executeAtomLauncherIntent()
                5 -> executeAviateLauncherIntent()
                6 -> executeLineageOSThemeEngineIntent()
                7 -> executeGoLauncherIntent()
                8, 18 -> executeIconPacksNotSupportedIntent()
                9 -> executeHoloLauncherIntent()
                10 -> executeHoloLauncherICSIntent()
                11 -> executeKkLauncherIntent()
                12 -> executeLgHomeLauncherIntent()
                13 -> executeLLauncherIntent()
                14 -> executeLucidLauncherIntent()
                15 -> executeMiniLauncherIntent()
                16 -> executeNextLauncherIntent()
                17 -> executeNovaLauncherIntent()
                19 -> executeSLauncherIntent()
                20 -> executeSmartLauncherIntent()
                21 -> executeSmartLauncherProIntent()
                22 -> executeSoloLauncherIntent()
                23 -> executeTsfLauncherIntent()
                24 -> executeUniconIntent()
            }
        }
    }
}

private fun Context.executeIconPacksNotSupportedIntent() {
    materialDialog {
        title(R.string.no_compatible_launcher_title)
        content(R.string.no_compatible_launcher_content)
        positiveText(android.R.string.ok)
        negativeText(android.R.string.cancel)
        onPositive { _, _ -> openLink(PLAY_STORE_LINK_PREFIX + "com.momocode.shortcuts") }
    }
}

private fun Context.executeActionLauncherIntent() {
    val action = packageManager.getLaunchIntentForPackage("com.actionlauncher.playstore")
    action.putExtra("apply_icon_pack", packageName)
    startActivity(action)
}

private fun Context.executeAdwLauncherIntent() {
    val intent = Intent("org.adw.launcher.SET_THEME")
    intent.putExtra("org.adw.launcher.theme.NAME", packageName)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

private fun Context.executeAdwEXLauncherIntent() {
    val intent = Intent("org.adwfreak.launcher.SET_THEME")
    intent.putExtra("org.adwfreak.launcher.theme.NAME", packageName)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

private fun Context.executeApexLauncherIntent() {
    val intent = Intent("com.anddoes.launcher.SET_THEME")
    intent.putExtra("com.anddoes.launcher.THEME_PACKAGE_NAME", packageName)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

private fun Context.executeAtomLauncherIntent() {
    val atom = Intent("com.dlto.atom.launcher.intent.action.ACTION_VIEW_THEME_SETTINGS")
    atom.`package` = "com.dlto.atom.launcher"
    atom.putExtra("packageName", packageName)
    atom.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(atom)
}

private fun Context.executeAviateLauncherIntent() {
    val aviate = Intent("com.tul.aviate.SET_THEME")
    aviate.`package` = "com.tul.aviate"
    aviate.putExtra("THEME_PACKAGE", packageName)
    aviate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(aviate)
}

private fun Context.executeGoLauncherIntent() {
    val intent = packageManager.getLaunchIntentForPackage("com.gau.go" + ".launcherex")
    val go = Intent("com.gau.go.launcherex.MyThemes.mythemeaction")
    go.putExtra("type", 1)
    go.putExtra("pkgname", packageName)
    sendBroadcast(go)
    startActivity(intent)
}

private fun Context.executeHoloLauncherIntent() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.component = ComponentName("com.mobint.hololauncher", "com.mobint.hololauncher.Settings")
    startActivity(intent)
}

private fun Context.executeHoloLauncherICSIntent() {
    val holohdApply = Intent(Intent.ACTION_MAIN)
    holohdApply.component = ComponentName("com.mobint.hololauncher.hd",
                                          "com.mobint" + ".hololauncher.SettingsActivity")
    startActivity(holohdApply)
}

private fun Context.executeKkLauncherIntent() {
    val kkApply = Intent("com.kk.launcher.APPLY_ICON_THEME")
    kkApply.putExtra("com.kk.launcher.theme.EXTRA_PKG", packageName)
    kkApply.putExtra("com.kk.launcher.theme.EXTRA_NAME", getString(R.string.app_name))
    startActivity(kkApply)
}

private fun Context.executeLgHomeLauncherIntent() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.component = ComponentName("com.lge.launcher2",
                                     "com.lge.launcher2.homesettings.HomeSettingsPrefActivity")
    startActivity(intent)
}

private fun Context.executeLLauncherIntent() {
    val l = Intent("com.l.launcher.APPLY_ICON_THEME", null)
    l.putExtra("com.l.launcher.theme.EXTRA_PKG", packageName)
    startActivity(l)
}

private fun Context.executeLineageOSThemeEngineIntent() {
    var themesAppInstalled = true
    val intent = Intent("android.intent.action.MAIN")
    
    if (isAppInstalled("org.cyanogenmod.theme.chooser")) {
        intent.component = ComponentName("org.cyanogenmod.theme.chooser",
                                         "org.cyanogenmod.theme.chooser.ChooserActivity")
    } else if (isAppInstalled("org.cyanogenmod.theme.chooser2")) {
        intent.component = ComponentName("org.cyanogenmod.theme.chooser2",
                                         "org.cyanogenmod.theme.chooser2.ChooserActivity")
    } else if (isAppInstalled("com.cyngn.theme.chooser")) {
        intent.component = ComponentName("com.cyngn.theme.chooser",
                                         "com.cyngn.theme.chooser.ChooserActivity")
    } else {
        themesAppInstalled = false
    }
    
    if (themesAppInstalled) {
        intent.putExtra("pkgName", packageName)
        try {
            startActivity(intent)
        } catch (e:Exception) {
            showToast(R.string.impossible_open_themes)
        }
    } else {
        showToast(R.string.themes_app_not_installed)
    }
}

private fun Context.executeLucidLauncherIntent() {
    val lucidApply = Intent("com.powerpoint45.action.APPLY_THEME", null)
    lucidApply.putExtra("icontheme", packageName)
    startActivity(lucidApply)
}

private fun Context.executeMiniLauncherIntent() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.component = ComponentName("com.jiubang.go.mini.launcher",
                                     "com.jiubang.go.mini.launcher.setting.MiniLauncherSettingActivity")
    startActivity(intent)
}

private fun Context.executeNextLauncherIntent() {
    var nextApply:Intent? = packageManager.getLaunchIntentForPackage("com.gtp.nextlauncher")
    if (nextApply == null) {
        nextApply = packageManager.getLaunchIntentForPackage("com.gtp.nextlauncher.trial")
    }
    val next = Intent("com.gau.go.launcherex.MyThemes.mythemeaction")
    next.putExtra("type", 1)
    next.putExtra("pkgname", packageName)
    sendBroadcast(next)
    startActivity(nextApply)
}

private fun Context.executeNovaLauncherIntent() {
    val intent = Intent("com.teslacoilsw.launcher.APPLY_ICON_THEME")
    intent.`package` = "com.teslacoilsw.launcher"
    intent.putExtra("com.teslacoilsw.launcher.extra.ICON_THEME_TYPE", "GO")
    intent.putExtra("com.teslacoilsw.launcher.extra.ICON_THEME_PACKAGE", packageName)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

private fun Context.executeSLauncherIntent() {
    val s = Intent("com.s.launcher.APPLY_ICON_THEME")
    s.putExtra("com.s.launcher.theme.EXTRA_PKG", packageName)
    s.putExtra("com.s.launcher.theme.EXTRA_NAME", getString(R.string.app_name))
    startActivity(s)
}

private fun Context.executeSmartLauncherIntent() {
    val smartLauncherIntent = Intent("ginlemon.smartlauncher.setGSLTHEME")
    smartLauncherIntent.putExtra("package", packageName)
    startActivity(smartLauncherIntent)
}

private fun Context.executeSmartLauncherProIntent() {
    val smartLauncherProIntent = Intent("ginlemon.smartlauncher.setGSLTHEME")
    smartLauncherProIntent.putExtra("package", packageName)
    startActivity(smartLauncherProIntent)
}

private fun Context.executeSoloLauncherIntent() {
    val soloApply = packageManager.getLaunchIntentForPackage("home.solo" + ".launcher.free")
    val solo = Intent("home.solo.launcher.free.APPLY_THEME")
    solo.putExtra("EXTRA_PACKAGENAME", packageName)
    solo.putExtra("EXTRA_THEMENAME", getString(R.string.app_name))
    sendBroadcast(solo)
    startActivity(soloApply)
}

private fun Context.executeTsfLauncherIntent() {
    val tsfApply = packageManager.getLaunchIntentForPackage("com.tsf.shell")
    val tsf = Intent("android.intent.action.MAIN")
    tsf.component = ComponentName("com.tsf.shell", "com.tsf.shell.ShellActivity")
    sendBroadcast(tsf)
    startActivity(tsfApply)
}

private fun Context.executeUniconIntent() {
    val unicon = Intent("android.intent.action.MAIN")
    unicon.addCategory("android.intent.category.LAUNCHER")
    unicon.`package` = "sg.ruqqq.IconThemer"
    startActivity(unicon)
}

val Context.defaultLauncher:Launcher?
    get() {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val launcherPackage = resolveInfo.activityInfo.packageName
        supportedLaunchers.forEach {
            if (it.hasPackage(launcherPackage.toString())) return it
        }
        return null
    }