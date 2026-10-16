package com.tabotabo.ui

import androidx.compose.runtime.Composable
import com.tabotabo.ui.theme.SystemBarsAppearance

/** Desktop windows have no system bars to tint. */
@Composable
internal actual fun rememberSystemBarsAppearance(): SystemBarsAppearance? = null
