package com.tabotabo.ui.layout

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Spacing multiplier applied by [SizeClass.Expanded]. */
private const val EXPANDED_SPACING_SCALE = 1.5f

/** Shorter-side width from which a screen counts as [SizeClass.Expanded]. */
internal val ExpandedThreshold: Dp = 600.dp

/** Coarse screen size, from the shorter side of the available space. */
public enum class SizeClass {
    /** Phones: shorter side below 600dp. */
    Compact,

    /** Tablets, desktops and wide windows: shorter side of 600dp or more. */
    Expanded,
}

/** Vertical spacing scale: 4 / 8 / 12 / 16 / 24 dp by default. */
@Immutable
public data class Spacing(
    val xs: Dp = 4.dp,
    val s: Dp = 8.dp,
    val m: Dp = 12.dp,
    val l: Dp = 16.dp,
    val xl: Dp = 24.dp,
) {
    /** Returns this scale with every step multiplied by [factor]. */
    internal fun scaled(factor: Float): Spacing =
        Spacing(xs * factor, s * factor, m * factor, l * factor, xl * factor)
}

/**
 * Dimension tokens.
 *
 * @property spacing Vertical spacing scale.
 * @property cardCorner Corner radius of cards and buttons.
 * @property outlineWidth Resting outline width.
 * @property highlightOutlineWidth Outline width when highlighted.
 * @property glowRadius Elevation of the colored glow on highlighted cards.
 * @property portraitMarginFraction Side margin as a fraction of the shorter screen side (0.08).
 * @property landscapeMarginFraction Same in landscape (0.14).
 */
@Immutable
public data class TabotaboDimensions(
    val spacing: Spacing = Spacing(),
    val cardCorner: Dp = 12.dp,
    val outlineWidth: Dp = 1.5.dp,
    val highlightOutlineWidth: Dp = 2.dp,
    val glowRadius: Dp = 16.dp,
    val portraitMarginFraction: Float = 0.08f,
    val landscapeMarginFraction: Float = 0.14f,
) {
    public companion object {
        /** Tokens for [sizeClass]; [SizeClass.Expanded] scales [spacing] up by 1.5×. */
        public fun default(sizeClass: SizeClass = SizeClass.Compact): TabotaboDimensions = when (sizeClass) {
            SizeClass.Compact -> TabotaboDimensions()
            SizeClass.Expanded -> TabotaboDimensions(spacing = Spacing().scaled(EXPANDED_SPACING_SCALE))
        }
    }
}

/**
 * Information a [ScreenScaffold] exposes to its slots.
 *
 * @property isLandscape Whether the scaffold is wider than tall.
 * @property sizeClass Size class from the scaffold's shorter side.
 * @property horizontalMargin Side margin the scaffold applied.
 * @property isWebLayout Whether the scaffold uses the single-column web layout.
 */
@Immutable
public data class ScreenLayoutInfo(
    val isLandscape: Boolean,
    val sizeClass: SizeClass,
    val horizontalMargin: Dp,
    val isWebLayout: Boolean = false,
)

/** Current layout info inside a [ScreenScaffold]; a compact portrait default outside one. */
public val LocalScreenLayoutInfo: ProvidableCompositionLocal<ScreenLayoutInfo> = staticCompositionLocalOf {
    ScreenLayoutInfo(isLandscape = false, sizeClass = SizeClass.Compact, horizontalMargin = 16.dp)
}

/** [SizeClass] of a space whose shorter side is [shorterSide]. */
internal fun sizeClassOf(shorterSide: Dp): SizeClass =
    if (shorterSide >= ExpandedThreshold) SizeClass.Expanded else SizeClass.Compact
