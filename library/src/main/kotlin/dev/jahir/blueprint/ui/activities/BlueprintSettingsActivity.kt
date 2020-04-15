package dev.jahir.blueprint.ui.activities

import dev.jahir.blueprint.BuildConfig
import dev.jahir.frames.ui.activities.SettingsActivity

class BlueprintSettingsActivity : SettingsActivity() {
    override val dashboardName: String = BuildConfig.DASHBOARD_NAME
    override val dashboardVersion: String = BuildConfig.DASHBOARD_VERSION
}