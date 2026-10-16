package com.tabotabo.ui

import androidx.compose.runtime.Composable
import com.tabotabo.ui.theme.SystemBarsAppearance

/** Browsers have no system bars to tint from inside the page. */
@Composable
internal actual fun rememberSystemBarsAppearance(): SystemBarsAppearance? = null
