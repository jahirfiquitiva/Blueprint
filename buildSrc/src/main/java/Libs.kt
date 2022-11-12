@file:Suppress("unused", "RemoveExplicitTypeArguments")

object Libs {
    // Kuper
    const val kuper = "dev.jahir:Kuper:${Versions.kuper}@aar"

    // Adaptive Icons
    private const val adaptiveIcons =
        "com.github.sarsamurmu:AdaptiveIconBitmap:${Versions.adaptiveIcons}"

    val dependencies = arrayOf<String>(adaptiveIcons)
}
