package com.tabotabo.website.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.settings.ShuffleColorsButton
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.surface.TabotaboDialog
import com.tabotabo.ui.surface.Tag
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.website.API_URL
import com.tabotabo.website.Appearance
import com.tabotabo.website.AppearanceControl
import com.tabotabo.website.Example
import com.tabotabo.website.Paragraph
import com.tabotabo.website.SHOWCASE_URL
import com.tabotabo.website.SitePage
import com.tabotabo.website.SitePageScaffold
import com.tabotabo.website.SubSection

/**
 * Components: how to use each one, as a code hint beside the live component, with a link to its
 * API reference. The rules behind them live on the principle pages, which each section links to.
 */
@Composable
internal fun ComponentsPage(hueState: HueState, appearance: Appearance, onAppearanceChange: (Appearance) -> Unit) {
    SitePageScaffold(page = SitePage.Components) {
        Paragraph {
            +"Each output below is the real component, live. The code is only a hint; the "
            link("API reference", API_URL)
            +" lists every parameter, and the "
            link("principles", SitePage.Principles)
            +" explain the rules behind them."
        }
        TypewriterSection()
        ButtonsSection()
        GlowCardsSection()
        PillsSection()
        DialogsSection()
        ScreenLayoutsSection()
        MotionSection()
        SettingsSection(hueState, appearance, onAppearanceChange)
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xl))
    }
}

@Composable
private fun TypewriterSection() {
    SubSection(title = { FocusedText("Typewriter headlines") }) {
        Paragraph {
            +"Build a headline from plain and focused segments. It types itself in once, without reflowing. "
            link("More on emphasis", SitePage.Text)
        }
        Example(
            code = """
                TypewriterText {
                    UnfocusedText("Where to")
                    FocusedText("next")
                    UnfocusedText("?")
                }
            """,
            apiPath = "com.tabotabo.ui.text/-typewriter-text.html",
        ) {
            TypewriterDemo {
                UnfocusedText("Where to")
                FocusedText("next")
                UnfocusedText("?")
            }
        }
    }
}

@Composable
private fun ButtonsSection() {
    SubSection(title = { FocusedText("Buttons") }) {
        Paragraph {
            +"Outlined and full width. The primary action is drawn in the primary color; everything else stays quiet. "
            link("Button rules", SitePage.Surfaces)
        }
        Example(
            code = """
                ActionStack {
                    PrimaryButton("Get started", onClick = ::start)
                    SecondaryButton("Settings", onClick = ::open)
                    SecondaryButton("Locked", onClick = {}, enabled = false)
                }
            """,
            apiPath = "com.tabotabo.ui.button/-primary-button.html",
        ) {
            ActionStack {
                PrimaryButton("Get started", onClick = {})
                SecondaryButton("Settings", onClick = {})
                SecondaryButton("Locked", onClick = {}, enabled = false)
            }
        }
    }
}

@Composable
private fun GlowCardsSection() {
    SubSection(title = { FocusedText("Glow cards") }) {
        Paragraph { +"Cards are outlines at rest. Highlight one and it turns primary and glows. Tap the card." }
        Example(
            code = """
                GlowCard(
                    highlighted = selected,
                    onClick = { selected = !selected },
                ) {
                    Text("Weekend in Lisbon")
                    Subtitle("4 days · 2 travellers")
                }
            """,
            apiPath = "com.tabotabo.ui.surface/-glow-card.html",
        ) {
            HighlightDemo()
        }
    }
}

@Composable
private fun PillsSection() {
    SubSection(title = { FocusedText("Pills and tags") }) {
        Paragraph { +"Pills choose between two or three options. Tags label things and never look tappable." }
        val options = listOf("Daily", "Weekly", "Monthly")
        var period by remember { mutableStateOf(options[1]) }
        Example(
            code = """
                SelectablePillRow(
                    options = listOf("Daily", "Weekly", "Monthly"),
                    selected = period,
                    onSelect = { period = it },
                    label = { it },
                )
                Tag("Beta")
            """,
            apiPath = "com.tabotabo.ui.surface/-selectable-pill-row.html",
        ) {
            SelectablePillRow(options = options, selected = period, onSelect = { period = it }, label = { it })
            Row(horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s)) {
                Tag("Beta")
                Tag("New", color = TabotaboTheme.colors.primary)
                Tag("Shared", color = TabotaboTheme.colors.distinct(1))
            }
        }
    }
}

@Composable
private fun DialogsSection() {
    SubSection(title = { FocusedText("Dialogs") }) {
        Paragraph { +"An outlined surface with an emphasis title and text actions in the primary color." }
        var open by remember { mutableStateOf(false) }
        Example(
            code = """
                TabotaboDialog(
                    onDismissRequest = { open = false },
                    confirmText = "View list",
                    onConfirm = ::openList,
                    dismissText = "Close",
                    title = { UnfocusedText("Changes"); FocusedText("saved") },
                )
            """,
            apiPath = "com.tabotabo.ui.surface/-tabotabo-dialog.html",
        ) {
            SecondaryButton("Open the dialog", onClick = { open = true }, modifier = Modifier.widthIn(max = 280.dp))
        }
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

@Composable
private fun ScreenLayoutsSection() {
    SubSection(title = { FocusedText("Screen layouts") }) {
        Paragraph {
            +"One scaffold handles portrait, landscape and the web: header on top, content in the middle, actions at "
            +"the bottom. Below is a real phone-sized screen; the "
            link("showcase", SHOWCASE_URL)
            +" shows the rest. "
            link("Layout rules", SitePage.Layout)
        }
        Example(
            code = """
                ScreenScaffold(
                    header = {
                        ScreenHeader(subtitle = "Pick a destination.") {
                            UnfocusedText("Plan the")
                            FocusedText("trip")
                        }
                    },
                    actions = { PrimaryButton("Continue", onClick = ::next) },
                ) {
                    DestinationPicker()
                }
            """,
            apiPath = "com.tabotabo.ui.layout/-screen-scaffold.html",
        ) {
            PhoneFrame()
        }
    }
}

@Composable
private fun MotionSection() {
    SubSection(title = { FocusedText("Motion") }) {
        Paragraph {
            +"Values pop in, working labels pulse, and every fifth tap here celebrates. "
            link("Motion tokens", SitePage.Motion)
        }
        Example(
            code = """
                PopInContent(targetState = count) { Text("${'$'}it") }
                Text("Thinking…", Modifier.pulse(active = true))
                Modifier.celebrate(trigger = milestone)
            """,
            apiPath = "com.tabotabo.ui.motion/-pop-in-content.html",
        ) {
            CounterDemo()
            ThinkingLabel()
        }
    }
}

@Composable
private fun SettingsSection(hueState: HueState, appearance: Appearance, onAppearanceChange: (Appearance) -> Unit) {
    SubSection(title = { FocusedText("Settings") }) {
        Paragraph {
            +"Every app ships appearance, a color shuffle and an About section. These controls are wired to this site, "
            +"so go ahead."
        }
        Example(
            code = """
                AppearanceSetting(
                    options = Appearance.entries,
                    selected = appearance,
                    onSelect = { appearance = it },
                    label = { it.label },
                    title = "Appearance",
                    description = "Pick light or dark, or follow your device.",
                )
                ShuffleColorsButton(hueState)
            """,
            apiPath = "com.tabotabo.ui.settings/-appearance-setting.html",
        ) {
            AppearanceControl(appearance, onAppearanceChange)
            ShuffleColorsButton(hueState = hueState, modifier = Modifier.fillMaxWidth())
        }
    }
}
