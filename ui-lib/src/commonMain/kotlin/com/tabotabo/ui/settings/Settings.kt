package com.tabotabo.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme

/**
 * Appearance picker: a titled [SelectablePillRow] with a plain-language description. The options
 * are the consumer's own type (e.g. their `Light / Dark / System` enum), so the library never
 * defines or resolves a "System" value.
 *
 * @param options Choices to show, in order (2–3 recommended).
 * @param selected Currently selected option.
 * @param onSelect Called with the tapped option; the consumer persists it and maps it to
 *   `TabotaboTheme`'s `darkTheme`.
 * @param label Display text for each option.
 */
@Composable
public fun <T> AppearanceSetting(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    SettingsSection(title = title, description = description, modifier = modifier) {
        SelectablePillRow(options = options, selected = selected, onSelect = onSelect, label = label)
    }
}

/** Secondary button that re-rolls the session hue via [HueState.shuffle]. */
@Composable
public fun ShuffleColorsButton(
    hueState: HueState,
    modifier: Modifier = Modifier,
    text: String = "Shuffle colors",
) {
    SecondaryButton(text = text, onClick = { hueState.shuffle() }, modifier = modifier)
}

/** Titled settings section with an optional muted [description] above its [content]. */
@Composable
public fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = TabotaboTheme.dimensions.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = title, color = TabotaboTheme.colors.accent, style = TabotaboTheme.typography.labelLarge)
        if (description != null) {
            Spacer(Modifier.height(spacing.xs))
            Subtitle(text = description, maxLines = Int.MAX_VALUE)
        }
        Spacer(Modifier.height(spacing.m))
        content()
    }
}
