package com.tabotabo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import com.tabotabo.ui.color.Hue
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.color.rememberHueState
import com.tabotabo.ui.motion.TabotaboMotion
import com.tabotabo.ui.theme.SystemBarsAppearance
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.tabotaboTypography

/**
 * [TabotaboTheme] with a fixed hue and the platform default font, so UI tests are deterministic
 * and don't depend on loading bundled font resources.
 */
@Composable
internal fun TestTheme(
    darkTheme: Boolean = true,
    hueState: HueState = rememberHueState { Hue(200f) },
    motion: TabotaboMotion = TabotaboMotion.Default,
    systemBars: SystemBarsAppearance? = null,
    content: @Composable () -> Unit,
) {
    TabotaboTheme(
        darkTheme = darkTheme,
        hueState = hueState,
        typography = tabotaboTypography(FontFamily.Default),
        motion = motion,
        systemBars = systemBars,
        content = content,
    )
}
