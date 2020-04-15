package dev.jahir.blueprint.extensions

import dev.jahir.frames.extensions.resources.lower
import java.text.Normalizer

// Icon name formatting
private const val SPACE = 1
private const val CAPS = 2
private const val CAPS_LOCK = 3

internal fun String.clean() =
    replace("[^\\w\\s]+".toRegex(), " ").trim().replace(" +".toRegex(), " ")
        .replace("\\p{Z}".toRegex(), "_").trim()

internal fun String.safeDrawableName(): String {
    val text = if (Character.isDigit(get(0))) ("a_$this") else this
    val normalized = Normalizer.normalize(text, Normalizer.Form.NFKD)
    val withoutAccents = normalized.replace("[\\p{InCombiningDiacriticalMarks}]", "")
    return withoutAccents.clean().lower().replace(" ", "_")
}

/**
 * Kotlin port of the icon names formatting method made by Aidan Follestad (afollestad)
 */
internal fun String.blueprintFormat(): String {
    val sb = StringBuilder()
    var underscoreMode = 0
    var foundFirstLetter = false
    var lastWasLetter = false
    var index = 0
    this.toCharArray().forEach {
        if (Character.isLetterOrDigit(it)) {
            if (underscoreMode == SPACE) {
                sb.append(" ")
                underscoreMode = CAPS
            }
            if (!foundFirstLetter && underscoreMode == CAPS) {
                sb.append(it)
            } else {
                sb.append(
                    if (index == 0 || underscoreMode > 1) Character.toUpperCase(it) else it
                )
            }
            if (underscoreMode < CAPS_LOCK) underscoreMode = 0
            foundFirstLetter = true
            lastWasLetter = true
        } else if (it == '_') {
            if (underscoreMode == CAPS_LOCK) {
                underscoreMode = if (lastWasLetter) {
                    SPACE
                } else {
                    sb.append(it)
                    0
                }
            } else {
                underscoreMode += 1
            }
            lastWasLetter = false
        }
        index += 1
    }
    return sb.toString()
}