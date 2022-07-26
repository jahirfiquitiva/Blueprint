package dev.jahir.blueprint.ui.activities

import dev.jahir.frames.ui.activities.AboutActivity
import dev.jahir.blueprint.BuildConfig

class BlueprintAboutActivity : AboutActivity() {
    override val dashboardName = BuildConfig.DASHBOARD_NAME
}
