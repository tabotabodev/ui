package com.tabotabo.website.pages.principles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.text.EmphasisText
import com.tabotabo.ui.text.EmphasisTextScope
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.text.TypewriterText
import com.tabotabo.ui.text.rememberTypewriterState
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.EmphasisWeights
import com.tabotabo.website.CodeHint
import com.tabotabo.website.LocalPhoneLayout
import com.tabotabo.website.Paragraph
import com.tabotabo.website.SitePage
import com.tabotabo.website.SitePageScaffold
import com.tabotabo.website.SpecTable
import com.tabotabo.website.SubSection
import com.tabotabo.website.ThemePreview
import kotlin.math.roundToInt

/** Longest text the playground accepts per field, so the headline stays a headline. */
private const val PLAYGROUND_MAX_CHARS = 60

/**
 * One role of the type scale.
 *
 * @property name The library's accessor (or the Material slot when it has none).
 * @property slot The Material 3 slot behind it.
 * @property emphasis Whether the sample is an emphasis line (Light + Bold) rather than plain text.
 * @property sample Plain words and the key phrase, or just the plain text.
 * @property style Reads the style from the live typography.
 */
private class TypeRole(
    val name: String,
    val slot: String,
    val emphasis: Boolean,
    val sample: Pair<String, String>,
    val style: @Composable () -> TextStyle,
)

private val TypeRoles = listOf(
    TypeRole("heroTitle", "displayLarge", true, "One look for" to "every app") { MaterialTheme.typography.displayLarge },
    TypeRole("headline", "displayMedium", true, "Where to" to "next?") { MaterialTheme.typography.displayMedium },
    TypeRole("screenTitle", "headlineLarge", true, "Plan the" to "trip") { MaterialTheme.typography.headlineLarge },
    TypeRole("headlineMedium", "headlineMedium", true, "Pick your" to "dates") { MaterialTheme.typography.headlineMedium },
    TypeRole("dialog title", "headlineSmall", true, "Changes" to "saved") { MaterialTheme.typography.headlineSmall },
    TypeRole("buttonLabel", "labelLarge", false, "Shuffle colors" to "") { MaterialTheme.typography.labelLarge },
    TypeRole("body", "bodyMedium", false, "Pick a fresh hue for this session." to "") { MaterialTheme.typography.bodyMedium },
    TypeRole("factTitle", "titleSmall", false, "App version" to "") { MaterialTheme.typography.titleSmall },
    TypeRole("caption", "bodySmall", false, "Colors change every launch." to "") { MaterialTheme.typography.bodySmall },
    TypeRole("label", "labelMedium", false, "System" to "") { MaterialTheme.typography.labelMedium },
    TypeRole("footnote", "labelSmall", false, "Saved a moment ago" to "") { MaterialTheme.typography.labelSmall },
)

/** The playground's role choices: label and how to read its style. */
private enum class PlaygroundRole(val label: String) { Hero("Hero"), Headline("Headline"), Title("Title") }

/** Text: one family, the type scale, the emphasis rule and a headline playground. */
@Composable
internal fun TextPage(hueState: HueState) {
    SitePageScaffold(page = SitePage.Text) {
        Paragraph {
            +"One family, Montserrat, in five weights. Hierarchy comes from weight and color, never from mixing fonts. "
            +"The library bundles it, or you can bring your own family per role."
        }
        WeightsSection()
        ScaleSection()
        EmphasisSection()
        PlaygroundSection(hueState)
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xl))
    }
}

// ---------------------------------------------------------------- Weights

@Composable
private fun WeightsSection() {
    val weights = listOf(
        Triple(FontWeight.Light, "Light 300", "The plain words of every title and headline"),
        Triple(FontWeight.Normal, "Regular 400", "Body text, captions, subtitles"),
        Triple(FontWeight.Medium, "Medium 500", "Titles of title / value facts"),
        Triple(FontWeight.Bold, "Bold 700", "Key phrases, labels, buttons"),
        Triple(FontWeight.Black, "Black 900", "Rare marks meant to read at a glance"),
    )
    SubSection(title = { UnfocusedText("Five"); FocusedText("weights") }) {
        Column(Modifier.padding(top = TabotaboTheme.dimensions.spacing.l), verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m)) {
            weights.forEach { (weight, name, use) ->
                Column {
                    Text(name, color = TabotaboTheme.colors.accent, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = weight))
                    Text(use, color = TabotaboTheme.colors.muted, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Scale

@Composable
private fun ScaleSection() {
    val phone = LocalPhoneLayout.current
    SubSection(title = { UnfocusedText("The type"); FocusedText("scale") }) {
        Paragraph {
            +"Every role at its real size, as this site renders it. Letter spacing is always zero, and titles balance "
            +"their line breaks."
            if (phone) {
                +" On phones this site steps its headings down one size, so the numbers here are smaller than the "
                +"library's defaults."
            }
        }
        Column(Modifier.padding(top = TabotaboTheme.dimensions.spacing.l)) {
            TypeRoles.forEach { role -> ScaleRow(role, phone) }
        }
    }
}

/** One role: the sample in its style, and its name, slot, size and weight read from that style. */
@Composable
private fun ScaleRow(role: TypeRole, phone: Boolean) {
    val colors = TabotaboTheme.colors
    val style = role.style()
    val spec = "${role.name} · ${role.slot}\n${style.fontSize.value.roundToInt()} / ${style.lineHeight.value.roundToInt()} · " +
        (if (role.emphasis) "Light + Bold" else weightName(style.fontWeight))
    val sample: @Composable (Modifier) -> Unit = { modifier ->
        if (role.emphasis) {
            EmphasisText(modifier = modifier, style = style, weights = EmphasisWeights.Title) {
                UnfocusedText(role.sample.first)
                FocusedText(role.sample.second)
            }
        } else {
            Text(role.sample.first, modifier, color = colors.accent, style = style)
        }
    }
    val info: @Composable (Modifier, TextAlign) -> Unit = { modifier, align ->
        Text(spec, modifier, color = colors.muted, style = MaterialTheme.typography.bodySmall, textAlign = align)
    }
    Column(Modifier.fillMaxWidth().padding(vertical = TabotaboTheme.dimensions.spacing.m)) {
        if (phone) {
            info(Modifier, TextAlign.Start)
            Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xs))
            sample(Modifier)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                sample(Modifier.weight(1f))
                Spacer(Modifier.width(TabotaboTheme.dimensions.spacing.l))
                info(Modifier.width(220.dp), TextAlign.End)
            }
        }
    }
}

private fun weightName(weight: FontWeight?): String = when (weight) {
    FontWeight.Light -> "Light"
    FontWeight.Medium -> "Medium"
    FontWeight.Bold -> "Bold"
    FontWeight.Black -> "Black"
    else -> "Regular"
}

// ---------------------------------------------------------------- Emphasis

@Composable
private fun EmphasisSection() {
    SubSection(title = { UnfocusedText("Emphasis by"); FocusedText("weight and color") }) {
        Paragraph {
            +"Every hero, headline and title is Light, with one key phrase in Bold and the primary color, like the "
            +"title of this section. A one-word title is emphasised whole. A muted subtitle sits right under it."
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        CodeHint(
            """
            focused:   Bold 700  + primary
            unfocused: Light 300 + accent
            """,
        )
        SpecTable(
            headers = listOf("Part", "Weight", "Color"),
            rows = listOf(
                listOf("Key phrase", "Bold 700", "primary"),
                listOf("Other words", "Light 300", "accent"),
                listOf("Subtitle underneath", "Regular 400 (body)", "muted"),
            ),
            weights = listOf(1.4f, 1f, 1f),
        )
        Paragraph {
            +"Keep the key phrase separate from the sentence around it, as a format string plus the phrase. "
            +"Translations can then move the phrase while the UI still knows what to emphasise."
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        CodeHint(
            """
            // strings: plan_title = "Plan the %s"   ·   de: "%s planen"
            TypewriterText { Formatted(stringResource(Res.string.plan_title), "trip") }
            """,
        )
    }
}

// ---------------------------------------------------------------- Playground

@Composable
private fun PlaygroundSection(hueState: HueState) {
    var template by remember { mutableStateOf("Plan the %s") }
    var phrase by remember { mutableStateOf("trip") }
    var role by remember { mutableStateOf(PlaygroundRole.Headline) }
    var replay by remember { mutableIntStateOf(0) }
    val placeholders = template.windowed(2).count { it == "%s" }
    val spacing = TabotaboTheme.dimensions.spacing

    SubSection(title = { UnfocusedText("Try"); FocusedText("it") }) {
        Paragraph { +"Write a headline with %s where the key phrase goes, then pick the phrase and a size." }
        Column(Modifier.padding(top = spacing.l).widthIn(max = 560.dp), verticalArrangement = Arrangement.spacedBy(spacing.m)) {
            PlaygroundField("Headline", template, { template = it.take(PLAYGROUND_MAX_CHARS) }, isError = placeholders != 1)
            if (placeholders != 1) {
                Text(
                    "Use %s exactly once, to mark where the key phrase goes.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            PlaygroundField("Key phrase", phrase, { phrase = it.take(PLAYGROUND_MAX_CHARS) })
            SelectablePillRow(PlaygroundRole.entries, role, { role = it }, label = { it.label })
        }
        ThemePreview(hueState) {
            val style = when (role) {
                PlaygroundRole.Hero -> MaterialTheme.typography.displayLarge
                PlaygroundRole.Headline -> MaterialTheme.typography.displayMedium
                PlaygroundRole.Title -> MaterialTheme.typography.headlineLarge
            }
            val line: EmphasisTextScope.() -> Unit = {
                if (placeholders == 1) Formatted(template, phrase) else UnfocusedText(template)
            }
            TypewriterText(state = rememberTypewriterState(key = replay), style = style, content = line)
            Subtitle("A muted subtitle sits right underneath.", maxLines = Int.MAX_VALUE)
            GlowCard(Modifier.fillMaxWidth()) {
                Text("Plain text stays Regular, like this card.", color = TabotaboTheme.colors.accent, style = MaterialTheme.typography.bodyMedium)
            }
            SecondaryButton("Replay", onClick = { replay++ }, modifier = Modifier.widthIn(max = 200.dp))
        }
    }
}

/** An outlined text field in the theme's colors; 16sp text so phones don't zoom in on focus. */
@Composable
private fun PlaygroundField(label: String, value: String, onValueChange: (String) -> Unit, isError: Boolean = false) {
    val colors = TabotaboTheme.colors
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = RoundedCornerShape(TabotaboTheme.dimensions.cardCorner),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.accent,
            unfocusedTextColor = colors.accent,
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outlineStrong,
            focusedLabelColor = colors.primary,
            unfocusedLabelColor = colors.muted,
            cursorColor = colors.primary,
        ),
    )
}
