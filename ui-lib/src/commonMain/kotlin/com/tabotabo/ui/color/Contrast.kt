package com.tabotabo.ui.color

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.abs

/** WCAG thresholds used by the color generator. */
public object ContrastTargets {
    /** Body text, labels and focused text. */
    public const val Text: Double = 4.5

    /** Highlighted borders, interactive control borders ([TabotaboColors.outlineStrong]) and large glyphs. */
    public const val Graphics: Double = 3.0
}

/** Lightness change per nudge step in [ensureContrast]. */
private const val LIGHTNESS_STEP = 0.01f

/** WCAG 2.x contrast ratio between two opaque colors, in `[1, 21]`. */
public fun contrastRatio(foreground: Color, background: Color): Double {
    val first = foreground.luminance()
    val second = background.luminance()
    return (maxOf(first, second) + 0.05) / (minOf(first, second) + 0.05)
}

/**
 * Returns this color with its HSL lightness stepped away from [against] (in 0.01 steps) until
 * its contrast is at least [target] against every color in [against], or until lightness hits
 * 0 or 1. Hue, saturation and alpha are kept. The direction is lighter when the first of
 * [against] is dark, darker otherwise.
 */
public fun Color.ensureContrast(target: Double, against: List<Color>): Color {
    if (against.isEmpty()) return this
    val hsl = toHsl()
    val towardLight = against.first().luminance() < 0.5f
    var lightness = hsl.lightness
    var color = this
    while (against.any { contrastRatio(color, it) < target }) {
        val next = (lightness + if (towardLight) LIGHTNESS_STEP else -LIGHTNESS_STEP).coerceIn(0f, 1f)
        if (next == lightness) break
        lightness = next
        color = Color.hsl(hsl.hue, hsl.saturation, lightness, alpha)
    }
    return color
}

/** An HSL triple: hue in degrees `[0, 360)`, saturation and lightness in `[0, 1]`. */
internal data class Hsl(val hue: Float, val saturation: Float, val lightness: Float)

/** Converts this color's sRGB channels to [Hsl]. */
internal fun Color.toHsl(): Hsl {
    val max = maxOf(red, green, blue)
    val min = minOf(red, green, blue)
    val lightness = (max + min) / 2f
    val delta = max - min
    if (delta == 0f) return Hsl(0f, 0f, lightness)
    val saturation = delta / (1f - abs(2f * lightness - 1f))
    val hue = when (max) {
        red -> 60f * (((green - blue) / delta) % 6f)
        green -> 60f * ((blue - red) / delta + 2f)
        else -> 60f * ((red - green) / delta + 4f)
    }
    return Hsl(Hue(hue).degrees, saturation.coerceIn(0f, 1f), lightness)
}
