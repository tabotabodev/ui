package com.tabotabo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tabotabo.ui.AppearanceChoice
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.layout.ScreenHeader
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.settings.AppearanceSetting
import com.tabotabo.ui.settings.SettingsSection
import com.tabotabo.ui.settings.ShuffleColorsButton
import com.tabotabo.ui.text.Fact
import com.tabotabo.ui.theme.TabotaboTheme

/** Version shown in the About section. */
private const val APP_VERSION = "1.0.0"

/**
 * Settings every app ships: appearance (Light / Dark / System), Shuffle colors, and About facts.
 * Navigation lives in the header's back arrow.
 *
 * @param webLayout Uses the single-column web layout, which already scrolls, so the content
 *   doesn't add its own scroll.
 */
@Composable
internal fun SettingsScreen(
    webLayout: Boolean,
    appearance: AppearanceChoice,
    onAppearanceChange: (AppearanceChoice) -> Unit,
    hueState: HueState,
    platformName: String,
    onBack: () -> Unit,
) {
    ScreenScaffold(
        webLayout = webLayout,
        header = {
            ScreenHeader(onBack = onBack, backContentDescription = "Back", subtitle = "Make it yours.") {
                FocusedText("Settings")
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (webLayout) Modifier else Modifier.verticalScroll(rememberScrollState())),
            verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.xl),
        ) {
            AppearanceSetting(
                options = AppearanceChoice.entries,
                selected = appearance,
                onSelect = onAppearanceChange,
                label = { it.label },
                title = "Appearance",
                description = "Pick light or dark, or follow your device.",
            )
            SettingsSection(
                title = "Colors",
                description = "Every launch picks a new color. Shuffle to try another one now.",
            ) {
                ShuffleColorsButton(hueState = hueState)
            }
            SettingsSection(title = "About") {
                Fact(title = "App version", value = APP_VERSION)
                Fact(title = "Platform", value = platformName)
            }
        }
    }
}
