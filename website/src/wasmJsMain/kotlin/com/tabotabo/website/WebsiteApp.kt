package com.tabotabo.website

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.color.rememberHueState
import com.tabotabo.ui.layout.ScreenHeader
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.settings.AppearanceSetting
import com.tabotabo.ui.settings.SettingsSection
import com.tabotabo.ui.settings.ShuffleColorsButton
import com.tabotabo.ui.surface.TabotaboDialog
import com.tabotabo.ui.theme.TabotaboTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** The viewer's appearance choice; persisted per browser, unlike the hue. */
internal enum class Appearance(val label: String, val storageValue: String?) {
    Light("Light", "light"),
    Dark("Dark", "dark"),
    System("System", null),
}

private const val APPEARANCE_KEY = "tabotabo-appearance"

/**
 * Website root: a fresh hue on every page load, the viewer's saved appearance, the browser's
 * theme color kept in sync, and the article in the library's single-column web layout.
 */
@Composable
internal fun WebsiteApp() {
    var appearance by remember {
        mutableStateOf(Appearance.entries.firstOrNull { it.storageValue == storageGet(APPEARANCE_KEY) } ?: Appearance.System)
    }
    val hueState = rememberHueState()
    var settingsOpen by remember { mutableStateOf(false) }
    val darkTheme = when (appearance) {
        Appearance.Light -> false
        Appearance.Dark -> true
        Appearance.System -> isSystemInDarkTheme()
    }
    val onAppearanceChange: (Appearance) -> Unit = {
        appearance = it
        storageSet(APPEARANCE_KEY, it.storageValue)
    }

    LaunchedEffect(Unit) { removeLoadingFallback() }

    TabotaboTheme(darkTheme = darkTheme, hueState = hueState, systemBars = BrowserThemeColor) {
        ScreenScaffold(
            webLayout = true,
            header = {
                ScreenHeader(
                    subtitle = "A Compose Multiplatform design system",
                    animateTitle = false,
                    actions = {
                        IconAction(onClick = { settingsOpen = true }, contentDescription = "Settings") { GearIcon() }
                    },
                ) {
                    UnfocusedText("Tabotabo")
                    FocusedText("UI")
                }
            },
        ) {
            // Lets readers select and copy any text, code samples included.
            SelectionContainer {
                Article(
                    hueState = hueState,
                    appearance = appearance,
                    onAppearanceChange = onAppearanceChange,
                )
            }
        }

        if (settingsOpen) {
            SettingsDialog(
                appearance = appearance,
                onAppearanceChange = onAppearanceChange,
                hueState = hueState,
                onClose = { settingsOpen = false },
            )
        }
    }
}

/** Appearance and color settings, opened from the header gear. */
@Composable
private fun SettingsDialog(
    appearance: Appearance,
    onAppearanceChange: (Appearance) -> Unit,
    hueState: HueState,
    onClose: () -> Unit,
) {
    TabotaboDialog(
        onDismissRequest = onClose,
        confirmText = "Done",
        onConfirm = onClose,
        title = { FocusedText("Settings") },
    ) {
        Column(Modifier.fillMaxWidth()) {
            AppearanceControl(appearance, onAppearanceChange)
            Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xl))
            SettingsSection(
                title = "Colors",
                description = "Every visit picks a new color. Shuffle to try another one now.",
            ) {
                ShuffleColorsButton(hueState = hueState, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/** The appearance picker, shared by the settings dialog and the article's settings example. */
@Composable
internal fun AppearanceControl(appearance: Appearance, onAppearanceChange: (Appearance) -> Unit) {
    AppearanceSetting(
        options = Appearance.entries,
        selected = appearance,
        onSelect = onAppearanceChange,
        label = { it.label },
        title = "Appearance",
        description = "Pick light or dark, or follow your device.",
    )
}

/** A settings gear drawn as simple strokes in `LocalContentColor`. */
@Composable
private fun GearIcon() {
    val color = LocalContentColor.current
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = size.minDimension * 0.11f
        val center = Offset(size.width / 2, size.height / 2)
        val ring = size.minDimension * 0.24f
        drawCircle(color, ring, center, style = Stroke(stroke))
        repeat(8) { index ->
            val angle = index * PI / 4
            val direction = Offset(cos(angle).toFloat(), sin(angle).toFloat())
            drawLine(color, center + direction * (ring + stroke), center + direction * (size.minDimension / 2 - stroke / 2), stroke, StrokeCap.Round)
        }
    }
}
