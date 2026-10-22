package com.tabotabo.website.pages.principles

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.website.Paragraph
import com.tabotabo.website.SitePage
import com.tabotabo.website.SitePageScaffold
import com.tabotabo.website.SpecTable
import com.tabotabo.website.SubSection
import com.tabotabo.website.pages.CounterDemo
import com.tabotabo.website.pages.HighlightDemo
import com.tabotabo.website.pages.ThinkingLabel
import com.tabotabo.website.pages.TypewriterDemo

/** Motion: what moves and why, a live demo of each, and the motion tokens. */
@Composable
internal fun MotionPage() {
    val spacing = TabotaboTheme.dimensions.spacing
    SitePageScaffold(page = SitePage.Motion) {
        Paragraph {
            +"A screen that never moves feels unfinished, so motion is expected. It stays quiet, and it uses only "
            +"Compose's own animation APIs. When the device asks to reduce motion, every animation becomes instant."
        }

        SubSection(title = { UnfocusedText("Headlines"); FocusedText("type in") }) {
            Paragraph {
                +"When a headline changes, it types in letter by letter. The full text is laid out first, so nothing "
                +"reflows while it types."
            }
            Spacer(Modifier.height(spacing.l))
            TypewriterDemo {
                UnfocusedText("Your")
                FocusedText("move")
            }
        }

        SubSection(title = { UnfocusedText("Values"); FocusedText("pop in") }) {
            Paragraph {
                +"A value that changes fades and scales in. Only a real celebration bounces: here, every fifth tap."
            }
            Spacer(Modifier.height(spacing.l))
            CounterDemo()
        }

        SubSection(title = { UnfocusedText("Highlights"); FocusedText("fade") }) {
            Paragraph { +"Outlines, widths and glow animate between resting and highlighted. Tap the card." }
            Spacer(Modifier.height(spacing.l))
            HighlightDemo()
        }

        SubSection(title = { UnfocusedText("Work"); FocusedText("pulses") }) {
            Paragraph { +"A label for something in progress gently pulses until the work is done." }
            Spacer(Modifier.height(spacing.l))
            ThinkingLabel()
        }

        SubSection(title = { UnfocusedText("Motion"); FocusedText("tokens") }) {
            Paragraph { +"Every duration and spring lives in one place, so the whole app can slow down or switch off together." }
            SpecTable(
                headers = listOf("Token", "Default", "Reduced motion", "Used by"),
                rows = listOf(
                    listOf("typewriterCharDelay", "35 ms per letter", "0", "Titles and status headlines"),
                    listOf("highlightMillis", "250 ms", "0", "Outlines, glow, button and pill colors"),
                    listOf("crossfadeMillis", "200 ms", "0", "Fades between states"),
                    listOf("popIn", "Spring from 80% scale", "Instant", "A value that changes"),
                    listOf("celebrate", "Bouncy spring to 115%", "Instant", "Real milestones only"),
                    listOf("pulse", "Opacity 100% to 45% and back, 700 ms each way", "Off", "Labels for work in progress"),
                ),
                weights = listOf(1.4f, 1.4f, 1f, 1.6f),
            )
        }
        Spacer(Modifier.height(spacing.xl))
    }
}
