package com.tabotabo.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.layout.CardList
import com.tabotabo.ui.layout.ScreenHeader
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.motion.PopInContent
import com.tabotabo.ui.motion.celebrate
import com.tabotabo.ui.motion.pulse
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.surface.Tag
import com.tabotabo.ui.surface.TabotaboDialog
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.heroTitle

/** The cards the showcase lists, one per family of components. */
private enum class ShowcaseItem { Highlight, Choices, Counter, DistinctColors, Working }

/** Every this-many taps, the counter card celebrates. */
private const val CELEBRATE_EVERY = 5

/**
 * A tour of the design system: an emphasis header, a list of component cards, and the screen's
 * actions. On the web, settings open from a header icon instead of a full-width button.
 */
@Composable
internal fun ShowcaseScreen(webLayout: Boolean, onOpenSettings: () -> Unit) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    ScreenScaffold(
        webLayout = webLayout,
        header = {
            ScreenHeader(
                subtitle = "One random hue, outlined cards, and type that carries the design.",
                subtitleMinLines = 2,
                actions = {
                    if (webLayout) {
                        IconAction(onClick = onOpenSettings, contentDescription = "Settings") { SettingsIcon() }
                    }
                },
            ) {
                UnfocusedText("Tabotabo")
                FocusedText("UI")
            }
        },
        actions = {
            PrimaryButton(text = "Open a dialog", onClick = { showDialog = true })
            if (!webLayout) SecondaryButton(text = "Settings", onClick = onOpenSettings)
        },
    ) {
        CardList(items = ShowcaseItem.entries, key = { it }) { item ->
            when (item) {
                ShowcaseItem.Highlight -> HighlightCard()
                ShowcaseItem.Choices -> ChoicesCard()
                ShowcaseItem.Counter -> CounterCard()
                ShowcaseItem.DistinctColors -> DistinctColorsCard()
                ShowcaseItem.Working -> WorkingCard()
            }
        }
    }

    if (showDialog) {
        TabotaboDialog(
            onDismissRequest = { showDialog = false },
            confirmText = "Got it",
            onConfirm = { showDialog = false },
            dismissText = "Close",
            title = { Formatted("Dialogs follow the %s rule", "emphasis") },
        ) {
            Subtitle(text = "An outlined surface, a left-aligned title, and primary text actions.", maxLines = Int.MAX_VALUE)
        }
    }
}

/** Title and muted description shared by every showcase card. */
@Composable
private fun CardHeading(title: String, description: String) {
    Text(text = title, color = TabotaboTheme.colors.accent, style = TabotaboTheme.typography.labelLarge)
    Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xs))
    Subtitle(text = description)
}

/** A card that glows while highlighted; tapping toggles it. */
@Composable
private fun HighlightCard() {
    var highlighted by rememberSaveable { mutableStateOf(false) }
    GlowCard(modifier = Modifier.fillMaxWidth(), highlighted = highlighted, onClick = { highlighted = !highlighted }) {
        CardHeading(
            title = if (highlighted) "Highlighted" else "Outlined card",
            description = "Tap to switch the outline to primary and glow.",
        )
    }
}

/** A 3-way choice as equal-width pills. */
@Composable
private fun ChoicesCard() {
    val options = listOf("Small", "Medium", "Large")
    var selected by rememberSaveable { mutableStateOf(options[1]) }
    GlowCard(modifier = Modifier.fillMaxWidth()) {
        CardHeading(title = "Selectable pills", description = "The selected one is outlined in primary.")
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        SelectablePillRow(options = options, selected = selected, onSelect = { selected = it }, label = { it })
    }
}

/** A big number that pops in on every tap and celebrates every [CELEBRATE_EVERY] taps. */
@Composable
private fun CounterCard() {
    var count by rememberSaveable { mutableIntStateOf(0) }
    GlowCard(modifier = Modifier.fillMaxWidth(), onClick = { count++ }) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Pop-in values", color = TabotaboTheme.colors.accent, style = TabotaboTheme.typography.labelLarge)
            Tag(text = "New")
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xs))
        Subtitle(text = "Tap to count; every fifth tap celebrates.")
        PopInContent(
            targetState = count,
            modifier = Modifier.fillMaxWidth().celebrate(count / CELEBRATE_EVERY),
        ) { value ->
            Text(text = value.toString(), color = TabotaboTheme.colors.primary, style = TabotaboTheme.typography.heroTitle)
        }
    }
}

/** Distinct golden-angle colors for things that must be told apart. */
@Composable
private fun DistinctColorsCard() {
    val colors = TabotaboTheme.colors
    GlowCard(modifier = Modifier.fillMaxWidth()) {
        CardHeading(title = "Distinct colors", description = "People and series step around the wheel.")
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        Row(horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s)) {
            listOf("Ana", "Ben", "Cleo", "Dev").forEachIndexed { index, name ->
                Tag(text = name, color = colors.distinct(index))
            }
        }
    }
}

/** A pulsing "working" label. */
@Composable
private fun WorkingCard() {
    GlowCard(modifier = Modifier.fillMaxWidth()) {
        CardHeading(title = "Working labels", description = "A gentle pulse while something is in progress.")
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(TabotaboTheme.colors.primary, CircleShape))
            Spacer(Modifier.size(TabotaboTheme.dimensions.spacing.s))
            Text(
                text = "Thinking…",
                color = TabotaboTheme.colors.primary,
                style = TabotaboTheme.typography.labelMedium,
                modifier = Modifier.pulse(active = true),
            )
        }
    }
}

/** A settings gear drawn as simple strokes in `LocalContentColor`. */
@Composable
private fun SettingsIcon() {
    val color = LocalContentColor.current
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = size.minDimension * 0.11f
        val center = Offset(size.width / 2, size.height / 2)
        val ring = size.minDimension * 0.24f
        drawCircle(color, ring, center, style = Stroke(stroke))
        repeat(8) { index ->
            val angle = index * kotlin.math.PI / 4
            val direction = Offset(kotlin.math.cos(angle).toFloat(), kotlin.math.sin(angle).toFloat())
            drawLine(
                color,
                center + direction * (ring + stroke),
                center + direction * (size.minDimension / 2 - stroke / 2),
                stroke,
                StrokeCap.Round,
            )
        }
    }
}
