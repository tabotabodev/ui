package com.tabotabo.website.pages.principles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.text.EmphasisText
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.EmphasisWeights
import com.tabotabo.website.LocalNavigate
import com.tabotabo.website.LocalPhoneLayout
import com.tabotabo.website.Paragraph
import com.tabotabo.website.SitePage
import com.tabotabo.website.SitePageScaffold
import com.tabotabo.website.SubSection
import com.tabotabo.website.pages.ThinkingLabel

/**
 * One principle on the intro page.
 *
 * @property page The topic's own page.
 * @property summary Two lines on what the principle says.
 * @property sample A tiny live illustration.
 */
private class Topic(val page: SitePage, val summary: String, val sample: @Composable () -> Unit)

private val Topics = listOf(
    Topic(SitePage.Color, "Every color comes from one random hue, and text is always readable.") { SwatchStrip() },
    Topic(SitePage.Text, "Montserrat only. Light words with the key phrase in Bold and the primary color.") { TextSample() },
    Topic(SitePage.Surfaces, "Cards and buttons are outlines. Glow only for the one thing that needs attention.") { SurfaceSample() },
    Topic(SitePage.Layout, "Left-aligned content on a fixed spacing scale, and nothing jumps.") { SpacingSample() },
    Topic(SitePage.Motion, "Expected but quiet: text types in, values pop in, work pulses.") { ThinkingLabel() },
)

/** Principles: why the system exists, then one card per topic. Details live on each topic's page. */
@Composable
internal fun PrinciplesPage() {
    SitePageScaffold(page = SitePage.Principles) {
        SubSection(title = { UnfocusedText("Why it"); FocusedText("exists") }) {
            Paragraph {
                +"Apps drift. Every new one starts from fresh defaults, and before long none of them look related. "
                +"Tabotabo UI writes one visual language down once, so that every new screen starts from the same place "
                +"instead of from scratch."
            }
            Paragraph {
                +"The look is minimal and editorial. There are no gradients, illustrations or decorative icons; type, a single "
                +"color and generous spacing do the work. That restraint is what makes it reusable: it suits a utility, a tracker "
                +"or a landing page equally well."
            }
            Paragraph {
                +"And it is multiplatform by design. One Compose code base renders the same components on Android, iOS, "
                +"desktop and the browser, so even this website is a Compose app."
            }
        }
        SubSection(title = { UnfocusedText("The"); FocusedText("principles") }) {
            TopicGrid()
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xl))
    }
}

/** The topic cards: one column on a phone, two when wide, equal heights per row. */
@Composable
private fun TopicGrid() {
    val spacing = TabotaboTheme.dimensions.spacing
    val columns = if (LocalPhoneLayout.current) 1 else 2
    Column(Modifier.padding(top = spacing.l), verticalArrangement = Arrangement.spacedBy(spacing.m)) {
        Topics.chunked(columns).forEach { row ->
            Row(Modifier.height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(spacing.m)) {
                row.forEach { topic -> TopicCard(topic, Modifier.weight(1f).fillMaxHeight()) }
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

/** A clickable card: topic name, summary, live sample, and a "Read more" link to the topic page. */
@Composable
private fun TopicCard(topic: Topic, modifier: Modifier) {
    val navigate = LocalNavigate.current
    val colors = TabotaboTheme.colors
    val spacing = TabotaboTheme.dimensions.spacing
    GlowCard(modifier = modifier, onClick = { navigate(topic.page) }) {
        Text(topic.page.navLabel, color = colors.accent, style = TabotaboTheme.typography.titleMedium)
        Spacer(Modifier.height(spacing.xs))
        Subtitle(topic.summary, maxLines = Int.MAX_VALUE)
        Spacer(Modifier.height(spacing.l))
        topic.sample()
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(spacing.l))
        Text("Read more", color = colors.primary, style = TabotaboTheme.typography.labelLarge)
    }
}

/** Six small swatches of the current scheme. */
@Composable
private fun SwatchStrip() {
    val colors = TabotaboTheme.colors
    val shape = RoundedCornerShape(6.dp)
    Row(horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.xs)) {
        listOf(colors.background, colors.surface, colors.primary, colors.accent, colors.muted, colors.outlineStrong).forEach {
            Box(Modifier.size(24.dp).clip(shape).background(it).border(1.dp, colors.outline, shape))
        }
    }
}

/** A Light + Bold sample headline. */
@Composable
private fun TextSample() {
    EmphasisText(style = TabotaboTheme.typography.headlineSmall, weights = EmphasisWeights.Title) {
        UnfocusedText("Plan the")
        FocusedText("trip")
    }
}

/** A resting and a highlighted outline, side by side. */
@Composable
private fun SurfaceSample() {
    val colors = TabotaboTheme.colors
    val dimensions = TabotaboTheme.dimensions
    val shape = RoundedCornerShape(dimensions.cardCorner)
    Row(horizontalArrangement = Arrangement.spacedBy(dimensions.spacing.s)) {
        Box(Modifier.size(width = 56.dp, height = 32.dp).clip(shape).background(colors.surface).border(dimensions.outlineWidth, colors.outline, shape))
        Box(Modifier.size(width = 56.dp, height = 32.dp).clip(shape).background(colors.surface).border(dimensions.highlightOutlineWidth, colors.primary, shape))
    }
}

/** The spacing scale as bars, 4 to 24dp. */
@Composable
private fun SpacingSample() {
    val colors = TabotaboTheme.colors
    val spacing = TabotaboTheme.dimensions.spacing
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
        listOf(4, 8, 12, 16, 24).forEach { value ->
            Box(Modifier.width(10.dp).height((value * 1.5f).dp).clip(RoundedCornerShape(2.dp)).background(colors.primary))
        }
    }
}
