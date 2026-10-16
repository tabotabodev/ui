package com.tabotabo.ui.motion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class MotionTest {

    @Test
    fun defaultMotionIsEnabled() {
        assertTrue(TabotaboMotion.Default.enabled)
        assertEquals(35.milliseconds, TabotaboMotion.Default.typewriterCharDelay)
    }

    @Test
    fun reducedMotionCollapsesEveryDuration() {
        val reduced = TabotaboMotion.Reduced
        assertFalse(reduced.enabled)
        assertEquals(Duration.ZERO, reduced.typewriterCharDelay)
        assertEquals(0, reduced.highlightMillis)
        assertEquals(0, reduced.crossfadeMillis)
    }
}
