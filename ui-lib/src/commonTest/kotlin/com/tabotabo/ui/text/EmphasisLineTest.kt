package com.tabotabo.ui.text

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.tabotabo.ui.typography.EmphasisWeights
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EmphasisLineTest {

    @Test
    fun segmentsGetASingleSpaceBetweenThem() {
        val line = buildEmphasisLine {
            UnfocusedText("Hello")
            FocusedText("User")
        }
        assertEquals("Hello User", line.text)
        assertEquals(listOf(FocusedRange(6, 10)), line.focused)
    }

    @Test
    fun existingWhitespaceSuppressesTheAutomaticSpace() {
        val line = buildEmphasisLine {
            UnfocusedText("Hello ")
            FocusedText("User")
        }
        assertEquals("Hello User", line.text)
    }

    @Test
    fun punctuationGetsNoSpaceBeforeIt() {
        val line = buildEmphasisLine {
            UnfocusedText("Who plays")
            FocusedText("what")
            UnfocusedText("?")
        }
        assertEquals("Who plays what?", line.text)
    }

    @Test
    fun noSpaceSuppressesTheNextSpace() {
        val line = buildEmphasisLine {
            UnfocusedText("Tic-Tac-")
            NoSpace()
            FocusedText("Toe")
        }
        assertEquals("Tic-Tac-Toe", line.text)
    }

    @Test
    fun spaceOverridesTheNextSpacing() {
        val line = buildEmphasisLine {
            UnfocusedText("A")
            Space(3)
            UnfocusedText("B")
            UnfocusedText("C")
        }
        assertEquals("A   B C", line.text)
    }

    @Test
    fun spaceRejectsNegativeCounts() {
        assertFailsWith<IllegalArgumentException> { buildEmphasisLine { Space(-1) } }
    }

    @Test
    fun emptySegmentsAreSkipped() {
        val line = buildEmphasisLine {
            UnfocusedText("A")
            FocusedText("")
            UnfocusedText("B")
        }
        assertEquals("A B", line.text)
        assertTrue(line.focused.isEmpty())
    }

    @Test
    fun aSingleFocusedSegmentIsFullyEmphasised() {
        val line = buildEmphasisLine { FocusedText("Settings") }
        assertEquals(listOf(FocusedRange(0, 8)), line.focused)
    }

    @Test
    fun formattedSplitsAroundThePlaceholder() {
        val line = buildEmphasisLine { Formatted("Set up the %s now", "match") }
        assertEquals("Set up the match now", line.text)
        assertEquals(listOf(FocusedRange(11, 16)), line.focused)
    }

    @Test
    fun formattedKeepsItsOwnSpacingAroundThePhrase() {
        val line = buildEmphasisLine { Formatted("¿Quién juega %s?", "qué") }
        assertEquals("¿Quién juega qué?", line.text)
    }

    @Test
    fun formattedIsAutoSpacedFromThePreviousSegment() {
        val line = buildEmphasisLine {
            UnfocusedText("Hey,")
            Formatted("%s here", "Ana")
        }
        assertEquals("Hey, Ana here", line.text)
        assertEquals(listOf(FocusedRange(5, 8)), line.focused)
    }

    @Test
    fun formattedRequiresExactlyOnePlaceholder() {
        assertFailsWith<IllegalArgumentException> { buildEmphasisLine { Formatted("no placeholder", "x") } }
        assertFailsWith<IllegalArgumentException> { buildEmphasisLine { Formatted("%s and %s", "x") } }
    }

    @Test
    fun touchingFocusedRangesWithTheSameColorMerge() {
        val line = buildEmphasisLine {
            FocusedText("Tic")
            NoSpace()
            FocusedText("Tac")
        }
        assertEquals(listOf(FocusedRange(0, 6)), line.focused)
    }

    @Test
    fun focusedRangesWithDifferentColorsStaySeparate() {
        val line = buildEmphasisLine {
            FocusedText("Red", Color.Red)
            NoSpace()
            FocusedText("Blue", Color.Blue)
        }
        assertEquals(2, line.focused.size)
    }

    @Test
    fun annotatedStringStylesOnlyTheFocusedRanges() {
        val line = buildEmphasisLine {
            UnfocusedText("Hello")
            FocusedText("User")
        }
        val annotated = line.toAnnotatedString(EmphasisWeights.Headline, focusedColor = Color.Red)
        assertEquals("Hello User", annotated.text)
        val span = annotated.spanStyles.single()
        assertEquals(6, span.start)
        assertEquals(10, span.end)
        assertEquals(FontWeight.Bold, span.item.fontWeight)
        assertEquals(Color.Red, span.item.color)
    }

    @Test
    fun annotatedStringPrefersASegmentColorOverride() {
        val line = buildEmphasisLine { FocusedText("Blue", Color.Blue) }
        val annotated = line.toAnnotatedString(EmphasisWeights.Headline, focusedColor = Color.Red)
        assertEquals(Color.Blue, annotated.spanStyles.single().item.color)
    }

    @Test
    fun linesWithTheSameContentAreEqual() {
        val build = { buildEmphasisLine { UnfocusedText("A"); FocusedText("B") } }
        assertEquals(build(), build())
    }
}
