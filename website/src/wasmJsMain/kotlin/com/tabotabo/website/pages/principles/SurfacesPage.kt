package com.tabotabo.website.pages.principles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.surface.SelectablePill
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.surface.TabotaboDialog
import com.tabotabo.ui.surface.Tag
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.website.CodeHint
import com.tabotabo.website.Paragraph
import com.tabotabo.website.SitePage
import com.tabotabo.website.SitePageScaffold
import com.tabotabo.website.SpecTable
import com.tabotabo.website.SubSection
import com.tabotabo.website.ThemePreview

/**
 * Surfaces: the rules behind cards, buttons, pills, tags and dialogs (which color, width and
 * state), each beside a live preview with the shared Light / Dark toggle. How to use each one in
 * code is on the Components page.
 */
@Composable
internal fun SurfacesPage(hueState: HueState) {
    SitePageScaffold(page = SitePage.Surfaces) {
        Paragraph {
            +"Cards and buttons are thin outlines one step off the background. Nothing casts a shadow at rest. Only the "
            +"one thing that needs attention lights up: its outline turns primary and may glow. The code for each "
            +"component is on the "
            link("Components", SitePage.Components)
            +" page."
        }
        CardsSection(hueState)
        ButtonsSection(hueState)
        PillsSection(hueState)
        DialogsSection(hueState)
        InteractionSection()
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xl))
    }
}

// ---------------------------------------------------------------- Cards

@Composable
private fun CardsSection(hueState: HueState) {
    SubSection(title = { FocusedText("Cards") }) {
        Paragraph {
            +"A card is the surface color with a 1.5dp outline and 12dp corners. Highlighting one changes three things "
            +"together. Use the glow sparingly: for the one card that needs attention, never for every selected item."
        }
        SpecTable(
            headers = listOf("State", "Fill", "Outline", "Glow"),
            rows = listOf(
                listOf("Resting", "surface", "outline, 1.5dp", "none"),
                listOf("Highlighted", "surface", "primary, 2dp", "16dp in primary, optional"),
            ),
            weights = listOf(1f, 1f, 1.2f, 1.4f),
        )
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        CodeHint("highlight: outline to primary · width 1.5dp to 2dp · glow 0 to 16dp   // all over 250 ms")
        ThemePreview(hueState) {
            var highlighted by remember { mutableStateOf(true) }
            GlowCard(Modifier.fillMaxWidth()) {
                Text("Resting card", color = TabotaboTheme.colors.accent, style = TabotaboTheme.typography.labelLarge)
                Subtitle("Surface fill, soft outline.", maxLines = Int.MAX_VALUE)
            }
            GlowCard(Modifier.fillMaxWidth(), highlighted = highlighted, onClick = { highlighted = !highlighted }) {
                Text(
                    if (highlighted) "Highlighted card" else "Tap to highlight",
                    color = TabotaboTheme.colors.accent,
                    style = TabotaboTheme.typography.labelLarge,
                )
                Subtitle("Primary outline and glow. Tap to switch.", maxLines = Int.MAX_VALUE)
            }
        }
    }
}

// ---------------------------------------------------------------- Buttons

@Composable
private fun ButtonsSection(hueState: HueState) {
    SubSection(title = { FocusedText("Buttons") }) {
        Paragraph {
            +"Buttons are outlined cards too: the fill is always the surface color, and the button's role sets the "
            +"outline and label. They are full width within the margins and stack vertically; labels never wrap."
        }
        SpecTable(
            headers = listOf("Button", "Outline", "Label"),
            rows = listOf(
                listOf("Primary", "primary", "primary, Bold"),
                listOf("Secondary", "outlineStrong (3:1)", "accent, Bold"),
                listOf("Disabled", "disabled", "disabled"),
                listOf("Icon action", "none", "primary"),
            ),
            weights = listOf(1f, 1.3f, 1.2f),
        )
        SpecTable(
            headers = listOf("Size", "Value"),
            rows = listOf(
                listOf("Corners", "12dp"),
                listOf("Outline", "1.5dp"),
                listOf("Height", "at least 52dp"),
                listOf("Padding", "20dp across, 14dp down"),
                listOf("Icon", "20dp, 8dp from the label"),
                listOf("Icon action", "48dp round touch area, no card"),
                listOf("Stack", "12dp apart; on the web at most 480dp wide"),
            ),
            weights = listOf(1f, 2f),
        )
        ThemePreview(hueState) {
            ActionStack {
                PrimaryButton("Continue", onClick = {})
                SecondaryButton("Not now", onClick = {})
                SecondaryButton("Unavailable", onClick = {}, enabled = false)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconAction(onClick = {}, contentDescription = "More information") { InfoIcon() }
                Spacer(Modifier.width(TabotaboTheme.dimensions.spacing.s))
                Subtitle("A bare icon action: no card, primary stroke.", maxLines = Int.MAX_VALUE)
            }
        }
    }
}

/** An "i" in a circle, drawn as strokes in `LocalContentColor`. */
@Composable
private fun InfoIcon() {
    val color = LocalContentColor.current
    Canvas(Modifier.size(24.dp)) {
        val stroke = size.minDimension * 0.09f
        drawCircle(color, size.minDimension * 0.42f, style = Stroke(stroke))
        val x = size.width / 2
        drawLine(color, Offset(x, size.height * 0.45f), Offset(x, size.height * 0.72f), stroke, StrokeCap.Round)
        drawCircle(color, stroke * 0.7f, Offset(x, size.height * 0.31f))
    }
}

// ---------------------------------------------------------------- Pills and tags

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PillsSection(hueState: HueState) {
    SubSection(title = { UnfocusedText("Pills and"); FocusedText("tags") }) {
        Paragraph {
            +"Pills pick one of two or three options, side by side at equal widths. The selected one is outlined in "
            +"primary, without glow. Tags only label things, so they must never look tappable."
        }
        SpecTable(
            headers = listOf("Element", "Outline", "Label"),
            rows = listOf(
                listOf("Pill, unselected", "outlineStrong, 1.5dp", "muted, Bold"),
                listOf("Pill, selected", "primary, 2dp", "primary, Bold"),
                listOf("Pill, disabled", "as its state", "disabled"),
                listOf("Tag", "its color (muted by default), 1.5dp", "same color, small Bold"),
            ),
            weights = listOf(1.2f, 1.6f, 1.2f),
        )
        ThemePreview(hueState) {
            val options = listOf("Daily", "Weekly", "Monthly")
            var period by remember { mutableStateOf(options[1]) }
            SelectablePillRow(options, period, { period = it }, label = { it })
            Row(horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s)) {
                SelectablePill("Locked", selected = false, onClick = {}, enabled = false, modifier = Modifier.weight(1f))
                SelectablePill("Locked", selected = true, onClick = {}, enabled = false, modifier = Modifier.weight(1f))
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s),
                verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s),
            ) {
                Tag("Beta")
                Tag("New", color = TabotaboTheme.colors.primary)
                Tag("Shared", color = TabotaboTheme.colors.distinct(1))
            }
        }
    }
}

// ---------------------------------------------------------------- Dialogs

@Composable
private fun DialogsSection(hueState: HueState) {
    SubSection(title = { FocusedText("Dialogs") }) {
        Paragraph {
            +"A dialog is an outlined surface over a scrim (the background at 60%). Its title follows the headline "
            +"rule, left-aligned, and its actions are text in the primary color."
        }
        SpecTable(
            headers = listOf("Part", "Style"),
            rows = listOf(
                listOf("Surface", "surface fill, outline 1.5dp, 12dp corners, 24dp padding"),
                listOf("Title", "Light + Bold, left-aligned"),
                listOf("Actions", "primary text, Bold, aligned to the end"),
                listOf("Behind it", "scrim: the background at 60% opacity"),
            ),
            weights = listOf(1f, 3f),
        )
        ThemePreview(hueState) {
            var open by remember { mutableStateOf(false) }
            SecondaryButton("Open a dialog", onClick = { open = true }, modifier = Modifier.widthIn(max = 280.dp))
            if (open) {
                TabotaboDialog(
                    onDismissRequest = { open = false },
                    confirmText = "View list",
                    onConfirm = { open = false },
                    dismissText = "Close",
                    title = { UnfocusedText("Changes"); FocusedText("saved") },
                ) {
                    Subtitle("Your packing list is up to date on every device.", maxLines = Int.MAX_VALUE)
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Interaction

@Composable
private fun InteractionSection() {
    SubSection(title = { UnfocusedText("Hover, focus and"); FocusedText("press") }) {
        Paragraph {
            +"There are no custom hover or focus styles. Every tappable surface uses Material's ripple in its content "
            +"color, which also shows hover and keyboard focus on desktop and the web. Try hovering or tabbing through "
            +"the previews above. Anything tappable shows it: a primary label, a primary outline, or an icon."
        }
    }
}
