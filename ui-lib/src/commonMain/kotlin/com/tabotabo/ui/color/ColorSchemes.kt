package com.tabotabo.ui.color

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/** Alpha of the scrim drawn behind dialogs and sheets. */
private const val SCRIM_ALPHA = 0.6f

/**
 * Maps the palette onto Material3 roles so stock M3 components follow the scheme. Secondary and
 * tertiary reuse [TabotaboColors.primary] (single hue), every surface container is
 * [TabotaboColors.surface], and `surfaceTint` is transparent so tonal elevation never tints.
 * M3 `outline` (important boundaries) is [TabotaboColors.outlineStrong] and `outlineVariant`
 * (decorative) is [TabotaboColors.outline]. The error roles are not overridden: they keep
 * Material's own light / dark values.
 */
public fun TabotaboColors.toColorScheme(): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = surface,
        onPrimaryContainer = primary,
        inversePrimary = primary,
        secondary = primary,
        onSecondary = onPrimary,
        secondaryContainer = surface,
        onSecondaryContainer = primary,
        tertiary = primary,
        onTertiary = onPrimary,
        tertiaryContainer = surface,
        onTertiaryContainer = primary,
        background = background,
        onBackground = accent,
        surface = surface,
        onSurface = accent,
        surfaceVariant = surface,
        onSurfaceVariant = muted,
        surfaceTint = Color.Transparent,
        inverseSurface = accent,
        inverseOnSurface = background,
        outline = outlineStrong,
        outlineVariant = outline,
        scrim = background.copy(alpha = SCRIM_ALPHA),
        surfaceBright = surface,
        surfaceContainer = surface,
        surfaceContainerHigh = surface,
        surfaceContainerHighest = surface,
        surfaceContainerLow = surface,
        surfaceContainerLowest = surface,
        surfaceDim = surface,
    )
}

/** Dark M3 scheme for [hue]. */
public fun tabotaboDarkColorScheme(hue: Hue): ColorScheme = tabotaboColors(hue, isDark = true).toColorScheme()

/** Light M3 scheme for [hue]. */
public fun tabotaboLightColorScheme(hue: Hue): ColorScheme = tabotaboColors(hue, isDark = false).toColorScheme()

/**
 * Generates a random dark scheme. Shorthand for `tabotaboDarkColorScheme(Hue.random(random))`.
 * Each call picks its own hue; build light and dark from one [Hue] when switching between them.
 */
public fun randomDarkColorScheme(random: Random = Random.Default): ColorScheme =
    tabotaboDarkColorScheme(Hue.random(random))

/**
 * Generates a random light scheme. Shorthand for `tabotaboLightColorScheme(Hue.random(random))`.
 * Each call picks its own hue; build light and dark from one [Hue] when switching between them.
 */
public fun randomLightColorScheme(random: Random = Random.Default): ColorScheme =
    tabotaboLightColorScheme(Hue.random(random))
