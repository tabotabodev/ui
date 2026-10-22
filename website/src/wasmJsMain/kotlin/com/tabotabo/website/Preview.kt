package com.tabotabo.website

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.theme.TabotaboTheme

/**
 * The Light / Dark choice of the live previews, shared by every preview for the whole visit.
 * Null until the visitor picks one: previews then follow the site's own appearance.
 */
@Stable
internal class PreviewModeState {
    /** True for dark, false for light, null to follow the site. */
    var dark: Boolean? by mutableStateOf(null)
}

/** The visit's [PreviewModeState]. */
internal val LocalPreviewMode = staticCompositionLocalOf { PreviewModeState() }

/** Width cap of the Light / Dark toggle. */
private val ToggleMaxWidth = 280.dp

/**
 * A live preview with a Light / Dark toggle above it. [content] runs in its own `TabotaboTheme`
 * for the chosen appearance (same hue, same type scale as the site), on that theme's background.
 */
@Composable
internal fun ThemePreview(hueState: HueState, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val mode = LocalPreviewMode.current
    val dark = mode.dark ?: TabotaboTheme.colors.isDark
    val spacing = TabotaboTheme.dimensions.spacing
    Column(modifier.fillMaxWidth().padding(top = spacing.l)) {
        Eyebrow("Preview")
        Spacer(Modifier.height(spacing.s))
        SelectablePillRow(
            options = listOf(false, true),
            selected = dark,
            onSelect = { mode.dark = it },
            label = { if (it) "Dark" else "Light" },
            modifier = Modifier.widthIn(max = ToggleMaxWidth),
        )
        Spacer(Modifier.height(spacing.m))
        TabotaboTheme(darkTheme = dark, hueState = hueState, typography = MaterialTheme.typography) {
            val colors = TabotaboTheme.colors
            val shape = RoundedCornerShape(16.dp)
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(colors.background)
                    .border(TabotaboTheme.dimensions.outlineWidth, colors.outline, shape)
                    .padding(spacing.l),
                verticalArrangement = Arrangement.spacedBy(spacing.m),
                content = content,
            )
        }
    }
}
