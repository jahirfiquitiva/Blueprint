package dev.jahir.blueprint.ui.activities

import dev.jahir.frames.ui.fragments.WallpapersFragment
import dev.jahir.kuper.ui.activities.KuperActivity

class BlueprintKuperActivity : KuperActivity() {
    override val wallpapersFragment: WallpapersFragment? = null
    override fun getLicKey(): String? = ""
}