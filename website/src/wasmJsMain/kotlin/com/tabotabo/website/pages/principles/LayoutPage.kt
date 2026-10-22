package com.tabotabo.website.pages.principles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.layout.Spacing
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.text.TypewriterText
import com.tabotabo.ui.text.rememberTypewriterState
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.website.CodeHint
import com.tabotabo.website.Paragraph
import com.tabotabo.website.SitePage
import com.tabotabo.website.SitePageScaffold
import com.tabotabo.website.SpecTable
import com.tabotabo.website.SubSection
import com.tabotabo.website.pages.PhoneFrame
import kotlin.math.roundToInt

/** Layout: alignment, the screen scaffold, the spacing scale, margins, size classes and no shifts. */
@Composable
internal fun LayoutPage() {
    SitePageScaffold(page = SitePage.Layout) {
        AlignmentSection()
        ScaffoldSection()
        SpacingSection()
        MarginsSection()
        NoShiftsSection()
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xl))
    }
}

@Composable
private fun AlignmentSection() {
    SubSection(title = { FocusedText("Left-aligned"); UnfocusedText(", almost always") }) {
        Paragraph {
            +"Titles, text, cards and lists start at the same left edge. Only two things are centred: buttons, and a "
            +"screen's single focal object, such as a chart or a big number, with its status line."
        }
    }
}

@Composable
private fun ScaffoldSection() {
    SubSection(title = { UnfocusedText("One scaffold,"); FocusedText("every shape") }) {
        Paragraph {
            +"In portrait, the header sits on top, content in the middle and actions at the bottom. In landscape, content "
            +"moves to the left and the header and actions stack on the right. On the web, it is one centred column. "
            +"The screen below is a real portrait scaffold; tap a card."
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.l))
        PhoneFrame()
    }
}

@Composable
private fun SpacingSection() {
    val compact = Spacing()
    SubSection(title = { UnfocusedText("A fixed"); FocusedText("spacing scale") }) {
        Paragraph {
            +"Five steps for all vertical spacing, drawn below at their real size. On tablets, desktops and wide windows "
            +"(a shorter side of 600dp or more), every step grows by half."
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.l))
        GlowCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m)) {
                listOf("xs" to compact.xs, "s" to compact.s, "m" to compact.m, "l" to compact.l, "xl" to compact.xl).forEach { (name, size) ->
                    SpacingRow(name, size)
                }
            }
        }
    }
}

/** One step of the scale: its name, a square at its compact size, and one at its expanded size. */
@Composable
private fun SpacingRow(name: String, size: Dp) {
    val colors = TabotaboTheme.colors
    val typography = TabotaboTheme.typography
    val expanded = size * 1.5f
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(name, Modifier.width(40.dp), color = colors.accent, style = typography.labelLarge)
        Box(Modifier.width(48.dp), contentAlignment = Alignment.CenterStart) { Square(size) }
        Text("${size.value.roundToInt()}dp", Modifier.width(56.dp), color = colors.muted, style = typography.bodySmall)
        Box(Modifier.width(48.dp), contentAlignment = Alignment.CenterStart) { Square(expanded, faded = true) }
        Text("${expanded.value.roundToInt()}dp wide", color = colors.muted, style = typography.bodySmall)
    }
}

@Composable
private fun Square(size: Dp, faded: Boolean = false) {
    val primary = TabotaboTheme.colors.primary
    Box(Modifier.size(size).clip(RoundedCornerShape(2.dp)).background(if (faded) primary.copy(alpha = 0.45f) else primary))
}

@Composable
private fun MarginsSection() {
    SubSection(title = { UnfocusedText("Margins that"); FocusedText("scale") }) {
        Paragraph {
            +"Side margins are a share of the screen's shorter side, so they feel the same on every device."
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        CodeHint(
            """
            sideMargin = shorterSide × (portrait ? 0.08 : 0.14)
            web: one centred column, at most 1080 wide, padding 6% of the width (20–96)
            """,
        )
        SpecTable(
            headers = listOf("Screen", "Shorter side", "Margin"),
            rows = listOf(
                listOf("Small phone, portrait", "360dp", "29dp"),
                listOf("Phone, portrait", "390dp", "31dp"),
                listOf("Phone, landscape", "390dp", "55dp"),
                listOf("Tablet, portrait", "800dp", "64dp"),
            ),
            weights = listOf(2f, 1f, 1f),
        )
    }
}

/** Status lines that change length; the reserved second line keeps everything below still. */
private val Statuses = listOf("Saving", "Uploading 3 photos to your shared album, almost done", "All set")

@Composable
private fun NoShiftsSection() {
    var index by remember { mutableIntStateOf(0) }
    SubSection(title = { UnfocusedText("Nothing"); FocusedText("jumps") }) {
        Paragraph {
            +"Anything that changes length or comes and goes gets its space reserved up front. Below, the status reserves "
            +"two lines, so the button never moves however long the text is."
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.l))
        GlowCard(Modifier.fillMaxWidth()) {
            TypewriterText(
                state = rememberTypewriterState(key = index),
                style = TabotaboTheme.typography.headlineSmall,
                minLines = 2,
                maxLines = 2,
            ) {
                FocusedText(Statuses[index])
            }
            Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.l))
            SecondaryButton(
                "Change the status",
                onClick = { index = (index + 1) % Statuses.size },
                modifier = Modifier.widthIn(max = 280.dp),
            )
        }
    }
}
