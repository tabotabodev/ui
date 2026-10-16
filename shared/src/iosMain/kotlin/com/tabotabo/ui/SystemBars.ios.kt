package com.tabotabo.ui

import androidx.compose.runtime.Composable
import com.tabotabo.ui.theme.SystemBarsAppearance

/** iOS status-bar style is owned by the hosting view controller, outside shared code. */
@Composable
internal actual fun rememberSystemBarsAppearance(): SystemBarsAppearance? = null
