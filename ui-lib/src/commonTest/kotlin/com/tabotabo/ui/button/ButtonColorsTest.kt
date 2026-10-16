package com.tabotabo.ui.button

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ButtonColorsTest {

    private val colors = TabotaboButtonColors(
        container = Color.Black,
        content = Color.Red,
        border = Color.Green,
        disabledContainer = Color.DarkGray,
        disabledContent = Color.Gray,
        disabledBorder = Color.LightGray,
    )

    @Test
    fun copyReplacesOnlyTheGivenValues() {
        val copy = colors.copy(content = Color.Blue)
        assertEquals(Color.Blue, copy.content)
        assertEquals(colors.border, copy.border)
        assertEquals(colors.disabledContent, copy.disabledContent)
    }

    @Test
    fun equalityIsStructural() {
        assertEquals(colors, colors.copy())
        assertEquals(colors.hashCode(), colors.copy().hashCode())
        assertNotEquals(colors, colors.copy(border = Color.Blue))
    }

    @Test
    fun stateAccessorsPickEnabledOrDisabledValues() {
        assertEquals(Color.Red, colors.content(enabled = true))
        assertEquals(Color.Gray, colors.content(enabled = false))
        assertEquals(Color.Green, colors.border(enabled = true))
        assertEquals(Color.LightGray, colors.border(enabled = false))
        assertEquals(Color.Black, colors.container(enabled = true))
        assertEquals(Color.DarkGray, colors.container(enabled = false))
    }
}
