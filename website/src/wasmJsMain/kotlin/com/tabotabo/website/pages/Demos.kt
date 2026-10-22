package com.tabotabo.website.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.layout.ScreenHeader
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.motion.PopInContent
import com.tabotabo.ui.motion.celebrate
import com.tabotabo.ui.motion.pulse
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.text.EmphasisTextScope
import com.tabotabo.ui.text.TypewriterText
import com.tabotabo.ui.text.rememberTypewriterState
import com.tabotabo.ui.theme.TabotaboTheme

/*
 * Live demos shared by several pages. Each is the real library component, not a picture of one.
 */

/** A phone-sized frame running a real portrait `ScreenScaffold`: header, cards and a pinned action. */
@Composable
internal fun PhoneFrame() {
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
                            Text(note, color = TabotaboTheme.colors.muted, style = TabotaboTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

/** A headline that types itself in, with a Replay button. */
@Composable
internal fun TypewriterDemo(content: EmphasisTextScope.() -> Unit) {
    var replay by remember { mutableIntStateOf(0) }
    TypewriterText(
        state = rememberTypewriterState(key = replay),
        style = TabotaboTheme.typography.headlineMedium,
        content = content,
    )
    SecondaryButton("Replay", onClick = { replay++ }, modifier = Modifier.widthIn(max = 200.dp))
}

/** A card counting taps: the number pops in, and every fifth tap celebrates. */
@Composable
internal fun CounterDemo() {
    var count by remember { mutableIntStateOf(0) }
    GlowCard(modifier = Modifier.fillMaxWidth(), onClick = { count++ }) {
        Text("Tap to count", color = TabotaboTheme.colors.muted, style = TabotaboTheme.typography.labelMedium)
        PopInContent(targetState = count, modifier = Modifier.celebrate(count / 5), contentAlignment = Alignment.CenterStart) {
            Text("$it", color = TabotaboTheme.colors.primary, style = TabotaboTheme.typography.displayLarge)
        }
    }
}

/** A "Thinking…" label with a dot, pulsing while work is in progress. */
@Composable
internal fun ThinkingLabel() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(TabotaboTheme.colors.primary))
        Spacer(Modifier.width(TabotaboTheme.dimensions.spacing.s))
        Text(
            "Thinking…",
            Modifier.pulse(active = true),
            color = TabotaboTheme.colors.primary,
            style = TabotaboTheme.typography.labelMedium,
        )
    }
}

/** A card that switches between resting and highlighted (with glow) when tapped. */
@Composable
internal fun HighlightDemo(title: String = "Weekend in Lisbon", note: String = "4 days · 2 travellers") {
    var highlighted by remember { mutableStateOf(true) }
    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        highlighted = highlighted,
        onClick = { highlighted = !highlighted },
    ) {
        Text(title, color = TabotaboTheme.colors.accent, style = TabotaboTheme.typography.labelLarge)
        Text(note, color = TabotaboTheme.colors.muted, style = TabotaboTheme.typography.bodySmall)
    }
}
