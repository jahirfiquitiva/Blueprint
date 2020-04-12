package dev.jahir.blueprint.data.models

import android.content.Context
import androidx.annotation.DrawableRes
import dev.jahir.blueprint.R
import dev.jahir.blueprint.extensions.blueprintFormat
import dev.jahir.blueprint.extensions.clean
import dev.jahir.kuper.extensions.isAppInstalled

@Suppress("ArrayInDataClass", "unused")
enum class Launcher(
    val appName: String,
    val packageNames: Array<String>,
    @DrawableRes val icon: Int,
    val isActuallySupported: Boolean = true
) {
    ACTION("Action Launcher", arrayOf("com.actionlauncher.playstore"), R.drawable.ic_action),
    ADW("ADW Launcher", arrayOf("org.adw.launcher"), R.drawable.ic_adw),
    ADW_EX("ADW Ex Launcher", arrayOf("org.adwfreak.launcher"), R.drawable.ic_adw_ex),
    APEX("Apex Launcher", arrayOf("com.anddoes.launcher"), R.drawable.ic_apex),
    GO("Go Launcher", arrayOf("com.gau.go.launcherex"), R.drawable.ic_go),
    GOOGLE_NOW(
        "Google Now Launcher",
        arrayOf("com.google.android.launcher"),
        R.drawable.ic_google_now,
        false
    ),
    HOLO("Holo Launcher", arrayOf("com.mobint.hololauncher"), R.drawable.ic_holo),
    HOLO_ICS("Holo Launcher ICS", arrayOf("com.mobint.hololauncher.hd"), R.drawable.ic_holo_ics),
    LG_HOME("LG Home", arrayOf("com.lge.launcher2"), R.drawable.ic_lg_home),
    LAWNCHAIR(
        "Lawnchair",
        arrayOf(
            "ch.deletescape.lawnchair.plah",
            "ch.deletescape.lawnchair",
            "ch.deletescape.lawnchair.ci",
            "ch.deletescape.lawnchair.dev"
        ),
        R.drawable.ic_lawnchair
    ),
    LINEAGE_OS(
        "LineageOS Theme Engine",
        arrayOf(
            "org.cyanogenmod.theme.chooser",
            "org.cyanogenmod.theme.chooser2",
            "com.cyngn.theme.chooser"
        ),
        R.drawable.ic_lineageos_theme_engine
    ),
    LUCID("Lucid Launcher", arrayOf("com.powerpoint45.launcher"), R.drawable.ic_lucid),
    NIAGARA("Niagara Launcher", arrayOf("bitpit.launcher"), R.drawable.ic_niagara),
    NOVA("Nova Launcher", arrayOf("com.teslacoilsw.launcher"), R.drawable.ic_nova),
    PIXEL(
        "Pixel Launcher",
        arrayOf("com.google.android.apps.nexuslauncher"),
        R.drawable.ic_pixel,
        false
    ),
    POSIDON("Posidon Launcher", arrayOf("posidon.launcher"), R.drawable.ic_posidon),
    SMART("Smart Launcher", arrayOf("ginlemon.flowerfree"), R.drawable.ic_smart),
    SMART_PRO("Smart Launcher Pro", arrayOf("ginlemon.flowerpro"), R.drawable.ic_smart_pro),
    SOLO("Solo Launcher", arrayOf("home.solo.launcher.free"), R.drawable.ic_solo),
    TSF("TSF Launcher", arrayOf("com.tsf.shell"), R.drawable.ic_tsf);

    val cleanAppName: String
        get() = appName.replace("launcher", "", true).clean().blueprintFormat()

    fun hasPackage(packageName: String): Boolean {
        packageNames.forEach { if (it.equals(packageName, true)) return true }
        return false
    }

    fun isInstalled(context: Context?): Boolean {
        context ?: return false
        return packageNames.any { context.isAppInstalled(it) }
    }

    companion object {
        fun getSupportedLaunchers(context: Context? = null): ArrayList<Pair<Launcher, Boolean>> {
            val default = values().sortedBy { it.appName }
            val pairs =
                default.map { Pair(it, it.isInstalled(context)) }.sortedByDescending { it.second }
            return ArrayList(pairs)
        }
    }
}