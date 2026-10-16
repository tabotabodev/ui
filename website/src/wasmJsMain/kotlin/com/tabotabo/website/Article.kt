package com.tabotabo.website

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.layout.ScreenHeader
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.motion.PopInContent
import com.tabotabo.ui.motion.celebrate
import com.tabotabo.ui.motion.pulse
import com.tabotabo.ui.settings.ShuffleColorsButton
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.surface.TabotaboDialog
import com.tabotabo.ui.surface.Tag
import com.tabotabo.ui.text.HeroTitle
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.text.TypewriterText
import com.tabotabo.ui.text.rememberTypewriterState
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.body
import com.tabotabo.ui.typography.buttonLabel
import com.tabotabo.ui.typography.caption
import com.tabotabo.ui.typography.footnote
import com.tabotabo.ui.typography.headline
import com.tabotabo.ui.typography.heroTitle
import com.tabotabo.ui.typography.label
import com.tabotabo.ui.typography.screenTitle
import kotlin.math.roundToInt

private const val GITHUB = "https://github.com/tabotabodev/ui"
private const val README = "$GITHUB#readme"

/** The whole website: one article, top to bottom. */
@Composable
internal fun Article(
    hueState: HueState,
    appearance: Appearance,
    onAppearanceChange: (Appearance) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Hero()
        Motivation()
        Principles(hueState)
        GettingStarted()
        ClaudeSkill()
        ComponentTour(appearance, onAppearanceChange, hueState)
        Explore()
        // Breathing room below the last section.
        Spacer(Modifier.height(48.dp))
    }
}

// ---------------------------------------------------------------- Hero

@Composable
private fun Hero() {
    val uriHandler = LocalUriHandler.current
    Column(Modifier.fillMaxWidth().padding(top = 32.dp)) {
        HeroTitle {
            UnfocusedText("One look for every app, from")
            FocusedText("a single hue")
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        Paragraph {
            +"Tabotabo UI is a design system for Compose Multiplatform. Type, one color and calm spacing carry the design, "
            +"so apps on Android, iOS, desktop and the web feel like one family. "
            bold("Everything on this page is drawn by the library itself")
            +", running as a Kotlin/Wasm app."
        }
        HeroActions(
            onShowcase = { uriHandler.openUri("showcase/") },
            onApi = { uriHandler.openUri("api/") },
            onSource = { uriHandler.openUri(GITHUB) },
        )
    }
}

/** Width from which the hero's three actions fit side by side. */
private val HeroActionsRowWidth = 800.dp

/**
 * The hero's actions: one row, each button sized to its label, when the column is wide enough;
 * otherwise a left-aligned stack of full-width buttons.
 */
@Composable
private fun HeroActions(onShowcase: () -> Unit, onApi: () -> Unit, onSource: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth().padding(top = 32.dp)) {
        if (maxWidth >= HeroActionsRowWidth) {
            Row(horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m)) {
                PrimaryButton("Try the live showcase", onClick = onShowcase)
                SecondaryButton("Read the API reference", onClick = onApi)
                SecondaryButton("View the source on GitHub", onClick = onSource)
            }
        } else {
            ActionStack(maxWidth = 480.dp, alignment = Alignment.Start) {
                PrimaryButton("Try the live showcase", onClick = onShowcase)
                SecondaryButton("Read the API reference", onClick = onApi)
                SecondaryButton("View the source on GitHub", onClick = onSource)
            }
        }
    }
}

// ---------------------------------------------------------------- Motivation

@Composable
private fun Motivation() {
    ArticleSection(eyebrow = "Motivation", title = { UnfocusedText("Why it"); FocusedText("exists") }) {
        Paragraph {
            +"Side projects drift. Every new app starts from fresh defaults, and before long none of them look related. "
            +"Tabotabo UI began as the visual language of a few personal apps and videos, written down once so that every "
            +"new screen starts from the same place instead of from scratch."
        }
        Paragraph {
            +"The look is minimal and editorial. There are no gradients, illustrations or decorative icons; type, a single "
            +"color and generous spacing do the work. That restraint is what makes it reusable: it suits a game, a utility "
            +"or a landing page equally well."
        }
        Paragraph {
            +"And it is multiplatform by design. One Compose code base renders the same components on Android, iOS, "
            +"desktop and the browser, and plain Jetpack Compose apps can use it too. Shipping a separate HTML and CSS kit "
            +"would mean maintaining the design twice, so even this website is a Compose app."
        }
    }
}

// ---------------------------------------------------------------- Principles

@Composable
private fun Principles(hueState: HueState) {
    ArticleSection(eyebrow = "Principles", title = { UnfocusedText("What makes it"); FocusedText("Tabotabo") }) {
        SubSection(title = { FocusedText("One hue"); UnfocusedText(", every visit") }) {
            Paragraph {
                +"Each launch picks a random hue, and every color in the interface derives from it: a near-black or "
                +"near-white background, a vivid primary, and text tinted with a hint of the same hue. Text colors are then "
                +"nudged until they pass "
                bold("WCAG 4.5:1 contrast")
                +" against both the background and cards, so no hue can produce unreadable text. Users choose light, dark "
                +"or system; the hue stays a surprise."
            }
            Paragraph { +"Shuffle it and watch this page change." }
            Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.l))
            Swatches(hueState)
        }

        SubSection(title = { FocusedText("Montserrat"); UnfocusedText(", and nothing else") }) {
            Paragraph {
                +"One family in many weights. Hierarchy comes from weight and color, never from mixing fonts. The library "
                +"bundles Montserrat, or you can bring your own family per role."
            }
            Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.l))
            TypeSpecimen()
        }

        SubSection(title = { UnfocusedText("Emphasis by"); FocusedText("weight and color") }) {
            Paragraph {
                +"Headlines are mostly regular, with one key phrase in bold and the primary color, like the title of this "
                +"section. Key phrases stay separate from the sentence around them, so translations can move them while "
                +"the UI still knows what to emphasise. Headlines that change type themselves in, letter by letter, without "
                +"ever shifting the layout."
            }
        }

        SubSection(title = { FocusedText("Outlined"); UnfocusedText(", not elevated") }) {
            Paragraph {
                +"Cards and buttons are thin outlines one step off the background. Nothing casts a shadow at rest. Only "
                +"the one thing that needs attention lights up: its outline turns primary and glows."
            }
        }

        SubSection(title = { UnfocusedText("Calm layouts,"); FocusedText("purposeful motion") }) {
            Paragraph {
                +"Content is left-aligned; only actions and a single focal object are centred. Screens adapt between "
                +"portrait, landscape and the web on their own, and nothing jumps when text changes. Motion is expected "
                +"but quiet: values pop in, highlights fade, working labels pulse, and only real celebrations bounce."
            }
        }
    }
}

/** Live swatches of the current scheme, with a shuffle button wired to the page's own hue. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Swatches(hueState: HueState) {
    val colors = TabotaboTheme.colors
    val roles = listOf(
        "background" to colors.background,
        "surface" to colors.surface,
        "primary" to colors.primary,
        "accent" to colors.accent,
        "muted" to colors.muted,
        "outline" to colors.outline,
        "distinct 1" to colors.distinct(1),
        "distinct 2" to colors.distinct(2),
    )
    GlowCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hue ${hueState.hue.degrees.roundToInt()}° · ${if (colors.isDark) "dark" else "light"}",
            color = colors.muted,
            style = TabotaboTheme.typography.label,
        )
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m),
            verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m),
        ) {
            roles.forEach { (name, color) -> Swatch(name, color) }
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.l))
        ShuffleColorsButton(hueState = hueState, modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth())
    }
}

@Composable
private fun Swatch(name: String, color: Color) {
    val shape = RoundedCornerShape(TabotaboTheme.dimensions.cardCorner)
    Column(Modifier.width(88.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(shape)
                .background(color)
                .border(TabotaboTheme.dimensions.outlineWidth, TabotaboTheme.colors.outline, shape),
        )
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xs))
        Text(name, color = TabotaboTheme.colors.muted, style = TabotaboTheme.typography.footnote)
    }
}

/** The type scale, set in the live typography. */
@Composable
private fun TypeSpecimen() {
    val typography = TabotaboTheme.typography
    val rows = listOf(
        "Hero · 42" to typography.heroTitle,
        "Headline · 34" to typography.headline,
        "Screen title · 32" to typography.screenTitle,
        "Button · 16 bold" to typography.buttonLabel,
        "Body · 14" to typography.body,
        "Caption · 13" to typography.caption,
        "Label · 13 bold" to typography.label,
        "Footnote · 11" to typography.footnote,
    )
    GlowCard(modifier = Modifier.fillMaxWidth()) {
        rows.forEach { (name, style) ->
            Column(Modifier.padding(vertical = TabotaboTheme.dimensions.spacing.xs)) {
                Text(name, color = TabotaboTheme.colors.muted, style = typography.footnote)
                Text("Plan the trip", color = TabotaboTheme.colors.accent, style = style, maxLines = 1)
            }
        }
    }
}

// ---------------------------------------------------------------- Getting started

@Composable
private fun GettingStarted() {
    ArticleSection(eyebrow = "Getting started", title = { UnfocusedText("Add it in"); FocusedText("one line") }) {
        Paragraph {
            +"Tabotabo UI is published on Maven Central. Add it to your shared module, or straight to an Android app:"
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        CodeHint("""implementation("io.github.tabotabodev:tabotabo-ui:0.1.0")""")
        Paragraph {
            +"Then wrap your UI in the theme. Your app decides light or dark; the library picks the colors."
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        CodeHint(
            """
            TabotaboTheme(darkTheme = isSystemInDarkTheme()) {
                App()
            }
            """,
        )
        Paragraph {
            +"The "
            link("README", README)
            +" walks through the rest: saving the appearance choice, tinting system bars, custom fonts, and every "
            +"component in detail."
        }
    }
}

// ---------------------------------------------------------------- Claude skill

@Composable
private fun ClaudeSkill() {
    val uriHandler = LocalUriHandler.current
    ArticleSection(eyebrow = "For Claude", title = { UnfocusedText("Teach"); FocusedText("Claude"); UnfocusedText("the system") }) {
        Paragraph {
            +"Tabotabo UI ships with a "
            bold("Claude skill")
            +": a short guide that teaches Claude how to set up the theme, which component fits which need, and the "
            +"design rules behind them. With it installed, asking Claude for \"a settings screen\" gives you one built from "
            +"the library, in the library's style."
        }
        SubSection(title = { UnfocusedText("In"); FocusedText("Claude Code") }) {
            Paragraph { +"Install it for every project on your machine:" }
            Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
            CodeHint(
                """
                mkdir -p ~/.claude/skills/tabotabo-ui
                curl -fsSL https://tabotabodev.github.io/ui/skill/SKILL.md \
                  -o ~/.claude/skills/tabotabo-ui/SKILL.md
                """,
            )
            Paragraph {
                +"Or commit it to a single repository under "
                bold(".claude/skills/tabotabo-ui/SKILL.md")
                +" so everyone working on that project gets it."
            }
        }
        SubSection(title = { UnfocusedText("In the"); FocusedText("Claude apps") }) {
            Paragraph { +"Download the skill as a zip and upload it from Claude's skills settings." }
            ActionStack(modifier = Modifier.padding(top = 24.dp), maxWidth = 480.dp, alignment = Alignment.Start) {
                SecondaryButton("Download the skill", onClick = { uriHandler.openUri("skill/tabotabo-ui-skill.zip") })
            }
        }
    }
}

// ---------------------------------------------------------------- Component tour

@Composable
private fun ComponentTour(appearance: Appearance, onAppearanceChange: (Appearance) -> Unit, hueState: HueState) {
    ArticleSection(eyebrow = "Components", title = { UnfocusedText("A"); FocusedText("quick tour") }) {
        Paragraph {
            +"Each output below is the real component, live. The code is only a hint; the "
            link("API reference", "api/")
            +" lists every parameter."
        }

        SubSection(title = { FocusedText("Typewriter headlines") }) {
            Paragraph { +"Build a headline from plain and focused segments. It types itself in once, without reflowing." }
            var replay by remember { mutableIntStateOf(0) }
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
                TypewriterText(
                    state = rememberTypewriterState(key = replay),
                    style = TabotaboTheme.typography.headlineMedium,
                ) {
                    UnfocusedText("Where to")
                    FocusedText("next")
                    UnfocusedText("?")
                }
                SecondaryButton("Replay", onClick = { replay++ }, modifier = Modifier.widthIn(max = 200.dp))
            }
        }

        SubSection(title = { FocusedText("Buttons") }) {
            Paragraph { +"Outlined and full width. The primary action is drawn in the primary color; everything else stays quiet." }
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

        SubSection(title = { FocusedText("Glow cards") }) {
            Paragraph { +"Cards are outlines at rest. Highlight one and it turns primary and glows. Tap the card." }
            var highlighted by remember { mutableStateOf(true) }
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
                GlowCard(
                    modifier = Modifier.fillMaxWidth(),
                    highlighted = highlighted,
                    onClick = { highlighted = !highlighted },
                ) {
                    Text("Weekend in Lisbon", color = TabotaboTheme.colors.accent, style = TabotaboTheme.typography.labelLarge)
                    Subtitle("4 days · 2 travellers")
                }
            }
        }

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

        SubSection(title = { FocusedText("Screen layouts") }) {
            Paragraph {
                +"One scaffold handles portrait, landscape and the web: header on top, content in the middle, actions at "
                +"the bottom. Below is a real phone-sized screen; the "
                link("showcase", "showcase/")
                +" shows the rest."
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

        SubSection(title = { FocusedText("Motion") }) {
            Paragraph { +"Values pop in, working labels pulse, and every fifth tap here celebrates." }
            var count by remember { mutableIntStateOf(0) }
            Example(
                code = """
                    PopInContent(targetState = count) { Text("${'$'}it") }
                    Text("Thinking…", Modifier.pulse(active = true))
                    Modifier.celebrate(trigger = milestone)
                """,
                apiPath = "com.tabotabo.ui.motion/-pop-in-content.html",
            ) {
                GlowCard(modifier = Modifier.fillMaxWidth(), onClick = { count++ }) {
                    Text("Tap to count", color = TabotaboTheme.colors.muted, style = TabotaboTheme.typography.label)
                    PopInContent(targetState = count, modifier = Modifier.celebrate(count / 5), contentAlignment = Alignment.CenterStart) {
                        Text("$it", color = TabotaboTheme.colors.primary, style = TabotaboTheme.typography.heroTitle)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(TabotaboTheme.colors.primary))
                    Spacer(Modifier.width(TabotaboTheme.dimensions.spacing.s))
                    Text("Thinking…", Modifier.pulse(active = true), color = TabotaboTheme.colors.primary, style = TabotaboTheme.typography.label)
                }
            }
        }

        SubSection(title = { FocusedText("Settings") }) {
            Paragraph {
                +"Every app ships appearance, a color shuffle and an About section. These controls are wired to this page, "
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
}

/** A phone-sized frame running a real [ScreenScaffold]. */
@Composable
private fun PhoneFrame() {
    val shape = RoundedCornerShape(20.dp)
    val destinations = listOf("Lisbon" to "4 days · coast", "Kyoto" to "6 days · temples")
    var selected by remember { mutableStateOf(destinations.first().first) }
    BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .width(minOf(maxWidth, 300.dp))
                .height(480.dp)
                .clip(shape)
                .border(TabotaboTheme.dimensions.outlineWidth, TabotaboTheme.colors.outline, shape),
        ) {
            ScreenScaffold(
                header = {
                    ScreenHeader(subtitle = "Pick a destination.") {
                        UnfocusedText("Plan the")
                        FocusedText("trip")
                    }
                },
                actions = { PrimaryButton("Continue", onClick = {}) },
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s),
                ) {
                    destinations.forEach { (place, note) ->
                        GlowCard(
                            modifier = Modifier.fillMaxWidth(),
                            highlighted = place == selected,
                            glow = false,
                            onClick = { selected = place },
                        ) {
                            Text(place, color = TabotaboTheme.colors.accent, style = TabotaboTheme.typography.labelLarge)
                            Text(note, color = TabotaboTheme.colors.muted, style = TabotaboTheme.typography.caption)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Explore

@Composable
private fun Explore() {
    val uriHandler = LocalUriHandler.current
    val destinations = listOf(
        Triple("Live showcase", "Every component in a running app.", "showcase/"),
        Triple("API reference", "Every type and parameter, generated from the source.", "api/"),
        Triple("README", "Installation, setup and usage for developers.", README),
        Triple("Source", "Issues, releases and the code itself.", GITHUB),
    )
    ArticleSection(eyebrow = "Explore", title = { UnfocusedText("Where to go"); FocusedText("next") }) {
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.l))
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val columns = if (maxWidth >= 640.dp) 2 else 1
            Column(verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m)) {
                destinations.chunked(columns).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m)) {
                        row.forEach { (title, description, url) ->
                            GlowCard(modifier = Modifier.weight(1f), onClick = { uriHandler.openUri(url) }) {
                                Text(title, color = TabotaboTheme.colors.primary, style = TabotaboTheme.typography.labelLarge)
                                Subtitle(description)
                            }
                        }
                        repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}
