package com.tabotabo.ui

import androidx.compose.runtime.Composable
import com.tabotabo.ui.theme.SystemBarsAppearance

/**
 * The platform's system-bar hook for `TabotaboTheme`, or null where the platform has no system
 * bars to tint from shared code.
 */
@Composable
internal expect fun rememberSystemBarsAppearance(): SystemBarsAppearance?
