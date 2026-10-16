package com.tabotabo.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import com.tabotabo.ui.theme.SystemBarsAppearance

/**
 * Switches the status and navigation bar icons between light and dark to match the scheme. The
 * bars themselves stay transparent (the activity is edge-to-edge), so the scheme's background
 * shows through.
 */
@Composable
internal actual fun rememberSystemBarsAppearance(): SystemBarsAppearance? {
    val view = LocalView.current
    return remember(view) {
        SystemBarsAppearance { colors ->
            if (view.isInEditMode) return@SystemBarsAppearance
            val window = view.context.findActivity()?.window ?: return@SystemBarsAppearance
            val lightBars = !colors.isDark
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val mask = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                window.insetsController?.setSystemBarsAppearance(if (lightBars) mask else 0, mask)
            } else {
                val flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = if (lightBars) {
                    window.decorView.systemUiVisibility or flags
                } else {
                    window.decorView.systemUiVisibility and flags.inv()
                }
            }
        }
    }
}

/** The [Activity] this context belongs to, unwrapping [ContextWrapper]s. */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
