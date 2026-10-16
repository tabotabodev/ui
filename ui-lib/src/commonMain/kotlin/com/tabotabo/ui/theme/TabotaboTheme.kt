package com.tabotabo.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.tabotabo.ui.color.Hue
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.color.TabotaboColors
import com.tabotabo.ui.color.rememberHueState
import com.tabotabo.ui.color.tabotaboColors
import com.tabotabo.ui.color.toColorScheme
import com.tabotabo.ui.layout.TabotaboDimensions
import com.tabotabo.ui.motion.TabotaboMotion
import com.tabotabo.ui.typography.montserratTypography

/** Hue of the fallback palette used outside a [TabotaboTheme] (e.g. bare previews). */
private const val FALLBACK_HUE = 210f

internal val LocalTabotaboColors = staticCompositionLocalOf { tabotaboColors(Hue(FALLBACK_HUE), isDark = true) }
internal val LocalTabotaboDimensions = staticCompositionLocalOf { TabotaboDimensions.default() }
internal val LocalTabotaboMotion = staticCompositionLocalOf { TabotaboMotion.Default }

/**
 * Root of the design system. Builds the [TabotaboColors] for [hueState]'s hue and [darkTheme],
 * and provides colors, typography, dimensions and motion through CompositionLocals. It also
 * installs a restyled [MaterialTheme] underneath, so stock M3 components pick up the scheme (no
 * tonal elevation tint).
 *
 * The library has no notion of a "System" appearance and never reads the OS setting. Consumers
 * resolve the user's choice into [darkTheme], e.g. `darkTheme = choice.isDark ?: isSystemInDarkTheme()`.
 *
 * @param darkTheme Whether to use the dark variant.
 * @param hueState Holds the session hue; defaults to a fresh random hue that survives
 *   recomposition and configuration changes but is regenerated on a cold start / page load.
 * @param typography Material [Typography]; defaults to [montserratTypography].
 * @param dimensions Spacing / shape / stroke tokens.
 * @param motion Animation tokens; pass [TabotaboMotion.Reduced] to honour OS reduce-motion.
 * @param systemBars Called whenever the resolved colors change (hue shuffle or [darkTheme]
 *   change). Platform code uses it to tint status/navigation bars.
 */
@Composable
public fun TabotaboTheme(
    darkTheme: Boolean,
    hueState: HueState = rememberHueState(),
    typography: Typography = montserratTypography(),
    dimensions: TabotaboDimensions = TabotaboDimensions.default(),
    motion: TabotaboMotion = TabotaboMotion.Default,
    systemBars: SystemBarsAppearance? = null,
    content: @Composable () -> Unit,
) {
    val hue = hueState.hue
    val colors = remember(hue, darkTheme) { tabotaboColors(hue, darkTheme) }
    val colorScheme = remember(colors) { colors.toColorScheme() }
    SideEffect { systemBars?.apply(colors) }

    MaterialTheme(colorScheme = colorScheme, typography = typography) {
        CompositionLocalProvider(
            LocalTabotaboColors provides colors,
            LocalTabotaboDimensions provides dimensions,
            LocalTabotaboMotion provides motion,
            LocalContentColor provides colors.accent,
            content = content,
        )
    }
}

/** Accessors for the current theme values, mirroring `MaterialTheme`. */
public object TabotaboTheme {
    /** The active palette. */
    public val colors: TabotaboColors
        @Composable @ReadOnlyComposable get() = LocalTabotaboColors.current

    /** The active Material typography. */
    public val typography: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography

    /** The active dimension tokens. */
    public val dimensions: TabotaboDimensions
        @Composable @ReadOnlyComposable get() = LocalTabotaboDimensions.current

    /** The active motion tokens. */
    public val motion: TabotaboMotion
        @Composable @ReadOnlyComposable get() = LocalTabotaboMotion.current
}

/**
 * Platform hook used to color system bars to match the active scheme. Android implements it
 * with `enableEdgeToEdge` / `WindowInsetsController`, iOS with the status-bar style; web may
 * set `<meta name="theme-color">`.
 */
public fun interface SystemBarsAppearance {
    /** @param colors The newly resolved scheme; `colors.isDark` selects light/dark bar icons. */
    public fun apply(colors: TabotaboColors)
}
