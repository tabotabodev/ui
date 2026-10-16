package com.tabotabo.ui.text

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.tabotabo.ui.TestTheme
import com.tabotabo.ui.motion.TabotaboMotion
import com.tabotabo.ui.typography.EmphasisWeights
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

/** Delay per character used by these tests: long enough that frame timing can't skew counts. */
private val CharDelay = 100.milliseconds

@OptIn(ExperimentalTestApi::class)
class TypewriterTextTest {

    @Test
    fun revealsSequentiallyAndFinishes() = runComposeUiTest {
        lateinit var state: TypewriterState
        var finishedCount = 0
        mainClock.autoAdvance = false
        setContent {
            TestTheme {
                state = rememberTypewriterState(charDelay = CharDelay, onFinished = { finishedCount++ })
                TypewriterText(state = state) {
                    UnfocusedText("Hello")
                    FocusedText("User")
                }
            }
        }
        mainClock.advanceTimeByFrame()
        assertEquals(0, state.revealedChars)

        mainClock.advanceTimeBy(350)
        // Only part of "Hello" is visible: "User" can't start before "Hello" finishes.
        assertTrue(state.revealedChars in 1 until 5, "revealed ${state.revealedChars}")
        assertTrue(!state.isFinished)

        mainClock.advanceTimeBy(2_000)
        assertEquals("Hello User".length, state.revealedChars)
        assertTrue(state.isFinished)
        assertEquals(1, finishedCount)
    }

    @Test
    fun semanticsExposeTheFullTextWhileTyping() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent {
            TestTheme {
                TypewriterText(state = rememberTypewriterState(charDelay = CharDelay)) {
                    UnfocusedText("Plan the")
                    FocusedText("trip")
                }
            }
        }
        mainClock.advanceTimeBy(150)
        onNodeWithText("Plan the trip").assertExists()
    }

    @Test
    fun skipRevealsEverythingImmediately() = runComposeUiTest {
        lateinit var state: TypewriterState
        mainClock.autoAdvance = false
        setContent {
            TestTheme {
                state = rememberTypewriterState(charDelay = CharDelay)
                TypewriterText(state = state) { UnfocusedText("A long status line") }
            }
        }
        mainClock.advanceTimeBy(150)
        runOnIdle { state.skip() }
        assertTrue(state.isFinished)
        assertEquals("A long status line".length, state.revealedChars)
    }

    @Test
    fun recompositionDoesNotReplayAFinishedReveal() = runComposeUiTest {
        lateinit var state: TypewriterState
        var finishedCount = 0
        var unrelated by mutableIntStateOf(0)
        mainClock.autoAdvance = false
        setContent {
            TestTheme {
                state = rememberTypewriterState(charDelay = CharDelay, onFinished = { finishedCount++ })
                // Reading `unrelated` here recomposes this scope whenever it changes.
                TypewriterText(state = state, minLines = 1 + unrelated % 1) { UnfocusedText("Done") }
            }
        }
        mainClock.advanceTimeBy(2_000)
        assertTrue(state.isFinished)

        unrelated++
        mainClock.advanceTimeBy(50)
        assertTrue(state.isFinished)
        assertEquals(4, state.revealedChars)
        assertEquals(1, finishedCount)
    }

    @Test
    fun changingTheTextTypesItInAgain() = runComposeUiTest {
        lateinit var state: TypewriterState
        var text by mutableStateOf("First")
        mainClock.autoAdvance = false
        setContent {
            TestTheme {
                state = rememberTypewriterState(charDelay = CharDelay)
                TypewriterText(state = state) { UnfocusedText(text) }
            }
        }
        mainClock.advanceTimeBy(2_000)
        assertTrue(state.isFinished)

        text = "Second line"
        mainClock.advanceTimeBy(250)
        assertTrue(state.revealedChars < "Second line".length, "revealed ${state.revealedChars}")

        mainClock.advanceTimeBy(2_000)
        assertEquals("Second line".length, state.revealedChars)
    }

    @Test
    fun restartTypesTheSameTextAgain() = runComposeUiTest {
        lateinit var state: TypewriterState
        mainClock.autoAdvance = false
        setContent {
            TestTheme {
                state = rememberTypewriterState(charDelay = CharDelay)
                TypewriterText(state = state) { UnfocusedText("Again") }
            }
        }
        mainClock.advanceTimeBy(2_000)
        assertTrue(state.isFinished)

        runOnIdle { state.restart() }
        mainClock.advanceTimeBy(150)
        assertTrue(state.revealedChars < 5, "revealed ${state.revealedChars}")
    }

    @Test
    fun reducedMotionRevealsInstantly() = runComposeUiTest {
        lateinit var state: TypewriterState
        mainClock.autoAdvance = false
        setContent {
            TestTheme(motion = TabotaboMotion.Reduced) {
                state = rememberTypewriterState()
                TypewriterText(state = state) { UnfocusedText("Instant") }
            }
        }
        mainClock.advanceTimeByFrame()
        assertTrue(state.isFinished)
    }

    @Test
    fun startDelayHoldsTheFirstCharacter() = runComposeUiTest {
        lateinit var state: TypewriterState
        mainClock.autoAdvance = false
        setContent {
            TestTheme {
                state = rememberTypewriterState(charDelay = CharDelay, startDelay = 1_000.milliseconds)
                TypewriterText(state = state) { UnfocusedText("Later") }
            }
        }
        mainClock.advanceTimeBy(800)
        assertEquals(0, state.revealedChars)
        mainClock.advanceTimeBy(3_000)
        assertTrue(state.isFinished)
    }

    @Test
    fun typingNeverRemeasuresTheText() = runComposeUiTest {
        lateinit var state: TypewriterState
        var layoutCount = 0
        mainClock.autoAdvance = false
        setContent {
            TestTheme {
                state = rememberTypewriterState(charDelay = CharDelay)
                TypewriterTextImpl(
                    modifier = Modifier,
                    state = state,
                    style = TextStyle(),
                    weights = EmphasisWeights.Headline,
                    baseColor = Color.White,
                    focusedColor = Color.Red,
                    textAlign = TextAlign.Start,
                    maxLines = Int.MAX_VALUE,
                    minLines = 1,
                    onTextLayout = { layoutCount++ },
                ) {
                    UnfocusedText("Measured")
                    FocusedText("once")
                }
            }
        }
        mainClock.advanceTimeByFrame()
        val layoutsBeforeTyping = layoutCount
        assertTrue(layoutsBeforeTyping > 0)

        mainClock.advanceTimeBy(3_000)
        assertTrue(state.isFinished)
        assertEquals(layoutsBeforeTyping, layoutCount)
    }
}
