package dev.jahir.blueprint.ui.activities

import dev.jahir.blueprint.BuildConfig
import dev.jahir.frames.ui.activities.AboutActivity

class BlueprintAboutActivity : AboutActivity() {
    override val dashboardName = BuildConfig.DASHBOARD_NAME
}
