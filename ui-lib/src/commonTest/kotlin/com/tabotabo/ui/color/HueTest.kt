package com.tabotabo.ui.color

import androidx.compose.runtime.saveable.SaverScope
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HueTest {

    @Test
    fun hueKeepsAnglesWithinOneTurn() {
        assertEquals(0f, Hue(0f).degrees)
        assertEquals(359.5f, Hue(359.5f).degrees)
    }

    @Test
    fun hueWrapsAnglesBeyondOneTurn() {
        assertEquals(10f, Hue(370f).degrees, 0.001f)
        assertEquals(0f, Hue(720f).degrees, 0.001f)
    }

    @Test
    fun hueWrapsNegativeAngles() {
        assertEquals(350f, Hue(-10f).degrees, 0.001f)
        assertEquals(270f, Hue(-450f).degrees, 0.001f)
    }

    @Test
    fun hueNeverReturnsAFullTurn() {
        assertTrue(Hue(-0.00001f).degrees < 360f)
    }

    @Test
    fun rotateWrapsAroundTheWheel() {
        assertEquals(20f, Hue(350f).rotate(30f).degrees, 0.001f)
        assertEquals(340f, Hue(10f).rotate(-30f).degrees, 0.001f)
    }

    @Test
    fun randomIsDeterministicForASeed() {
        assertEquals(Hue.random(Random(42)), Hue.random(Random(42)))
    }

    @Test
    fun randomStaysWithinOneTurn() {
        val random = Random(7)
        repeat(1_000) {
            val degrees = Hue.random(random).degrees
            assertTrue(degrees >= 0f && degrees < 360f, "out of range: $degrees")
        }
    }

    @Test
    fun distanceToTakesTheShortWayRound() {
        assertEquals(20f, Hue(350f).distanceTo(Hue(10f)), 0.001f)
        assertEquals(180f, Hue(0f).distanceTo(Hue(180f)), 0.001f)
    }
}

class HueStateTest {

    @Test
    fun hueCanBeSetDirectly() {
        val state = HueState(Hue(100f))
        state.hue = Hue(-30f)
        assertEquals(330f, state.hue.degrees, 0.001f)
    }

    @Test
    fun shuffleMovesAwayFromADirectlySetHue() {
        val state = HueState(Hue(0f))
        state.hue = Hue(200f)
        state.shuffle(Random(5), minDistance = 45f)
        assertTrue(state.hue.distanceTo(Hue(200f)) >= 45f - 0.001f)
    }

    @Test
    fun shuffleMovesAtLeastTheMinimumDistance() {
        val state = HueState(Hue(100f))
        val random = Random(1)
        repeat(500) {
            val before = state.hue
            state.shuffle(random, minDistance = 45f)
            assertTrue(before.distanceTo(state.hue) >= 45f - 0.01f, "moved only ${before.distanceTo(state.hue)}")
        }
    }

    @Test
    fun shuffleClampsAnOversizedMinimumToTheOppositeHue() {
        val state = HueState(Hue(30f))
        state.shuffle(Random(3), minDistance = 500f)
        assertEquals(210f, state.hue.degrees, 0.01f)
    }

    @Test
    fun saverRoundTripsTheHue() {
        val scope = object : SaverScope {
            override fun canBeSaved(value: Any): Boolean = true
        }
        val saved = with(HueState.Saver) { scope.save(HueState(Hue(123.5f))) }
        val restored = HueState.Saver.restore(saved!!)
        assertEquals(123.5f, restored!!.hue.degrees)
    }
}
