package dev.jahir.blueprint.data.models

import android.content.Context
import androidx.annotation.DrawableRes
import dev.jahir.blueprint.R
import dev.jahir.blueprint.extensions.blueprintFormat
import dev.jahir.blueprint.extensions.clean
import dev.jahir.frames.extensions.context.stringArray
import dev.jahir.kuper.extensions.isAppInstalled

@Suppress("ArrayInDataClass", "unused")
enum class Launcher(
    val key: String,
    val appName: String,
    val packageNames: Array<String>,
    @DrawableRes val icon: Int,
    val isActuallySupported: Boolean = true
) {
    ACTION(
        "action",
        "Action Launcher",
        arrayOf("com.actionlauncher.playstore"),
        R.drawable.ic_action
    ),
    ADW("adw", "ADW Launcher", arrayOf("org.adw.launcher"), R.drawable.ic_adw),
    ADW_EX("adwex", "ADW Ex Launcher", arrayOf("org.adwfreak.launcher"), R.drawable.ic_adw_ex),
    APEX("apex", "Apex Launcher", arrayOf("com.anddoes.launcher"), R.drawable.ic_apex),
    GO("go", "Go Launcher", arrayOf("com.gau.go.launcherex"), R.drawable.ic_go),
    GOOGLE_NOW(
        "googlenow",
        "Google Now Launcher",
        arrayOf("com.google.android.launcher"),
        R.drawable.ic_google_now,
        false
    ),
    HOLO("holo", "Holo Launcher", arrayOf("com.mobint.hololauncher"), R.drawable.ic_holo),
    HOLO_ICS(
        "holoics",
        "Holo Launcher ICS",
        arrayOf("com.mobint.hololauncher.hd"),
        R.drawable.ic_holo_ics
    ),
    LG_HOME("lg", "LG Home", arrayOf("com.lge.launcher2"), R.drawable.ic_lg_home),
    LAWNCHAIR(
        "lawnchair",
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
        "lineageos",
        "LineageOS Theme Engine",
        arrayOf(
            "org.cyanogenmod.theme.chooser",
            "org.cyanogenmod.theme.chooser2",
            "com.cyngn.theme.chooser"
        ),
        R.drawable.ic_lineageos_theme_engine
    ),
    LUCID("lucid", "Lucid Launcher", arrayOf("com.powerpoint45.launcher"), R.drawable.ic_lucid),
    NIAGARA("niagara", "Niagara Launcher", arrayOf("bitpit.launcher"), R.drawable.ic_niagara),
    NOVA("nova", "Nova Launcher", arrayOf("com.teslacoilsw.launcher"), R.drawable.ic_nova),
    ONEPLUS("oneplus", "OnePlus Launcher", arrayOf("net.oneplus.launcher"), R.drawable.ic_oneplus),
    PIXEL(
        "pixel",
        "Pixel Launcher",
        arrayOf("com.google.android.apps.nexuslauncher"),
        R.drawable.ic_pixel,
        false
    ),
    POSIDON("posidon", "Posidon Launcher", arrayOf("posidon.launcher"), R.drawable.ic_posidon),
    SMART("smart", "Smart Launcher", arrayOf("ginlemon.flowerfree"), R.drawable.ic_smart),
    SMART_PRO(
        "smartpro",
        "Smart Launcher Pro",
        arrayOf("ginlemon.flowerpro"),
        R.drawable.ic_smart_pro
    ),
    SOLO("solo", "Solo Launcher", arrayOf("home.solo.launcher.free"), R.drawable.ic_solo),
    SQUARE(
        "square",
        "Square Home Launcher",
        arrayOf("com.ss.squarehome2"),
        R.drawable.ic_square_home
    ),
    TSF("tsf", "TSF Launcher", arrayOf("com.tsf.shell"), R.drawable.ic_tsf);

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
            val actuallySupportedLaunchers =
                context?.stringArray(R.array.supported_launchers).orEmpty()
            val default = values().sortedBy { it.appName }
                .filter { def -> actuallySupportedLaunchers.any { sup -> sup == def.key } }
            val pairs =
                default.map { Pair(it, it.isInstalled(context)) }.sortedByDescending { it.second }
            return ArrayList(pairs)
        }
    }
}