package com.tabotabo.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.tabotabo.ui.color.rememberHueState
import com.tabotabo.ui.screens.SettingsScreen
import com.tabotabo.ui.screens.ShowcaseScreen
import com.tabotabo.ui.theme.TabotaboTheme

/** The user's appearance choice. The app owns it; the library only receives `darkTheme`. */
enum class AppearanceChoice(val label: String) {
    Light("Light"),
    Dark("Dark"),
    System("System"),
}

/** Screens of the app, switched in place. */
private enum class Screen { Showcase, Settings }

/**
 * App root shared by every platform: resolves the appearance choice into `darkTheme`, owns the
 * session hue, wires the platform's system-bar hook into [TabotaboTheme], and switches between
 * the showcase and settings screens.
 */
@Composable
@Preview
fun App() {
    var appearance by rememberSaveable { mutableStateOf(AppearanceChoice.System) }
    var screen by rememberSaveable { mutableStateOf(Screen.Showcase) }
    val hueState = rememberHueState()
    val platform = remember { getPlatform() }
    val darkTheme = when (appearance) {
        AppearanceChoice.Light -> false
        AppearanceChoice.Dark -> true
        AppearanceChoice.System -> isSystemInDarkTheme()
    }

    TabotaboTheme(
        darkTheme = darkTheme,
        hueState = hueState,
        systemBars = rememberSystemBarsAppearance(),
    ) {
        when (screen) {
            Screen.Showcase -> ShowcaseScreen(
                webLayout = platform.isWeb,
                onOpenSettings = { screen = Screen.Settings },
            )
            Screen.Settings -> SettingsScreen(
                webLayout = platform.isWeb,
                appearance = appearance,
                onAppearanceChange = { appearance = it },
                hueState = hueState,
                platformName = platform.name,
                onBack = { screen = Screen.Showcase },
            )
        }
    }
}
