package com.tabotabo.ui.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Hue step between consecutive [TabotaboColors.distinct] colors: the golden angle. */
internal const val GOLDEN_ANGLE: Float = 137.5f

/**
 * The design system's full palette. It is a superset of what M3's `ColorScheme` can express:
 * [accent] and [muted] have no exact M3 slot.
 *
 * @property hue The hue every role is derived from.
 * @property isDark Whether this is the dark variant.
 * @property background Screen background: near-black / near-white, faintly tinted.
 * @property surface Card and button fill, one step off [background].
 * @property primary Emphasis: focused text, highlighted outlines, primary actions.
 * @property onPrimary Content drawn on a [primary] fill (rare; buttons are outlined).
 * @property accent Baseline text: near-white / near-black with a hint of the hue.
 * @property muted Subtitles, descriptions, inactive labels.
 * @property outline Card and control borders.
 * @property disabled Content and outline of disabled controls.
 */
@Immutable
public data class TabotaboColors(
    val hue: Hue,
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val primary: Color,
    val onPrimary: Color,
    val accent: Color,
    val muted: Color,
    val outline: Color,
    val disabled: Color,
) {
    /**
     * Returns a color for the [index]-th item that must be told apart from the others (people,
     * chart series, tags). The hue steps by the golden angle (137.5°) from [primary], uses the
     * primary's tone, and gets the same contrast nudging. Index 0 is [primary].
     */
    public fun distinct(index: Int): Color {
        require(index >= 0) { "index must be non-negative, was $index" }
        if (index == 0) return primary
        val tone = Tones.forMode(isDark).primary
        return tone.toColor(hue.rotate(index * GOLDEN_ANGLE))
            .ensureContrast(ContrastTargets.Text, listOf(background, surface))
    }
}

/** A saturation/lightness pair (HSL, `0..1`), applied to whichever hue is being generated. */
internal data class Tone(val saturation: Float, val lightness: Float) {
    fun toColor(hue: Hue): Color = Color.hsl(hue.degrees, saturation, lightness)
}

/** The [Tone] of each color role in one appearance (light or dark). */
internal class Tones(
    val background: Tone,
    val surface: Tone,
    val primary: Tone,
    val accent: Tone,
    val muted: Tone,
    val outline: Tone,
) {
    companion object {
        val Dark = Tones(
            background = Tone(0.60f, 0.04f),
            surface = Tone(0.35f, 0.10f),
            primary = Tone(0.95f, 0.58f),
            accent = Tone(0.45f, 0.88f),
            muted = Tone(0.25f, 0.62f),
            outline = Tone(0.20f, 0.24f),
        )
        val Light = Tones(
            background = Tone(0.45f, 0.97f),
            surface = Tone(0.30f, 0.995f),
            primary = Tone(0.85f, 0.40f),
            accent = Tone(0.35f, 0.14f),
            muted = Tone(0.18f, 0.40f),
            outline = Tone(0.20f, 0.82f),
        )

        fun forMode(isDark: Boolean): Tones = if (isDark) Dark else Light
    }
}

/** Alpha applied to [TabotaboColors.muted] for [TabotaboColors.disabled]. */
private const val DISABLED_ALPHA = 0.38f

/**
 * Builds the full palette for [hue] from the skill's HSL tone table, then nudges every text
 * role's lightness away from the background until it reaches [ContrastTargets.Text] against
 * both background and surface.
 */
public fun tabotaboColors(hue: Hue, isDark: Boolean): TabotaboColors {
    val tones = Tones.forMode(isDark)
    val background = tones.background.toColor(hue)
    val surface = tones.surface.toColor(hue)
    fun readable(tone: Tone) = tone.toColor(hue).ensureContrast(ContrastTargets.Text, listOf(background, surface))
    val muted = readable(tones.muted)
    return TabotaboColors(
        hue = hue,
        isDark = isDark,
        background = background,
        surface = surface,
        primary = readable(tones.primary),
        onPrimary = background,
        accent = readable(tones.accent),
        muted = muted,
        outline = tones.outline.toColor(hue),
        disabled = muted.copy(alpha = DISABLED_ALPHA),
    )
}
