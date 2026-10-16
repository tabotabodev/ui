package com.tabotabo.ui.color

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** Shortest angular distance between two hue angles, in degrees. */
private fun angleBetween(first: Float, second: Float): Float {
    val difference = abs(first - second) % 360f
    return minOf(difference, 360f - difference)
}

class ContrastTest {

    @Test
    fun blackOnWhiteIsTheMaximumRatio() {
        assertEquals(21.0, contrastRatio(Color.Black, Color.White), 0.01)
    }

    @Test
    fun equalColorsHaveNoContrast() {
        assertEquals(1.0, contrastRatio(Color.Gray, Color.Gray), 0.0001)
    }

    @Test
    fun contrastIsSymmetric() {
        val red = Color(0xFFD03030)
        assertEquals(contrastRatio(red, Color.White), contrastRatio(Color.White, red), 0.0001)
    }

    @Test
    fun ensureContrastLightensOnDarkBackgrounds() {
        val background = Color(0xFF101010)
        val result = Color(0xFF303030).ensureContrast(ContrastTargets.Text, listOf(background))
        assertTrue(contrastRatio(result, background) >= ContrastTargets.Text)
        assertTrue(result.toHsl().lightness > Color(0xFF303030).toHsl().lightness)
    }

    @Test
    fun ensureContrastDarkensOnLightBackgrounds() {
        val background = Color(0xFFF5F5F5)
        val result = Color(0xFFC0C0C0).ensureContrast(ContrastTargets.Text, listOf(background))
        assertTrue(contrastRatio(result, background) >= ContrastTargets.Text)
        assertTrue(result.toHsl().lightness < Color(0xFFC0C0C0).toHsl().lightness)
    }

    @Test
    fun ensureContrastKeepsAlreadyReadableColors() {
        assertEquals(Color.White, Color.White.ensureContrast(ContrastTargets.Text, listOf(Color.Black)))
    }

    @Test
    fun ensureContrastWithNothingToContrastAgainstIsANoOp() {
        val color = Color(0xFF336699)
        assertEquals(color, color.ensureContrast(ContrastTargets.Text, emptyList()))
    }

    @Test
    fun toHslReadsPrimaryHues() {
        assertEquals(0f, Color.Red.toHsl().hue, 0.5f)
        assertEquals(120f, Color.Green.toHsl().hue, 0.5f)
        assertEquals(240f, Color.Blue.toHsl().hue, 0.5f)
        assertEquals(0.5f, Color.Red.toHsl().lightness, 0.01f)
    }
}

class TabotaboColorsTest {

    private val textRoles = listOf<Pair<String, (TabotaboColors) -> Color>>(
        "primary" to { it.primary },
        "accent" to { it.accent },
        "muted" to { it.muted },
    )

    @Test
    fun everyHueKeepsTextReadableInBothModes() {
        for (degrees in 0 until 360) {
            for (isDark in listOf(true, false)) {
                val colors = tabotaboColors(Hue(degrees.toFloat()), isDark)
                for ((role, pick) in textRoles) {
                    val color = pick(colors)
                    for (surface in listOf(colors.background, colors.surface)) {
                        val ratio = contrastRatio(color, surface)
                        assertTrue(ratio >= ContrastTargets.Text, "$role at hue $degrees dark=$isDark: $ratio")
                    }
                }
                assertTrue(contrastRatio(colors.primary, colors.background) >= ContrastTargets.Graphics)
            }
        }
    }

    @Test
    fun darkSchemeSitsOnANearBlackBackground() {
        val colors = tabotaboColors(Hue(120f), isDark = true)
        assertTrue(colors.background.toHsl().lightness < 0.1f)
        assertTrue(colors.isDark)
    }

    @Test
    fun lightSchemeSitsOnANearWhiteBackground() {
        val colors = tabotaboColors(Hue(120f), isDark = false)
        assertTrue(colors.background.toHsl().lightness > 0.9f)
    }

    @Test
    fun primaryCarriesTheSchemeHue() {
        val colors = tabotaboColors(Hue(200f), isDark = true)
        assertTrue(angleBetween(colors.primary.toHsl().hue, 200f) < 3f)
        assertEquals(Hue(200f), colors.hue)
    }

    @Test
    fun disabledIsTranslucentMuted() {
        val colors = tabotaboColors(Hue(50f), isDark = false)
        assertEquals(colors.muted.copy(alpha = 0.38f), colors.disabled)
    }

    @Test
    fun onPrimaryIsTheBackground() {
        val colors = tabotaboColors(Hue(300f), isDark = true)
        assertEquals(colors.background, colors.onPrimary)
    }

    @Test
    fun distinctZeroIsPrimary() {
        val colors = tabotaboColors(Hue(10f), isDark = true)
        assertEquals(colors.primary, colors.distinct(0))
    }

    @Test
    fun distinctStepsByTheGoldenAngle() {
        val colors = tabotaboColors(Hue(10f), isDark = true)
        for (index in 1..5) {
            val expected = (10f + index * GOLDEN_ANGLE) % 360f
            val actual = colors.distinct(index).toHsl().hue
            assertTrue(angleBetween(actual, expected) < 3f, "index $index: $actual vs $expected")
        }
    }

    @Test
    fun distinctColorsStayReadable() {
        for (isDark in listOf(true, false)) {
            val colors = tabotaboColors(Hue.random(Random(5)), isDark)
            for (index in 0..8) {
                val color = colors.distinct(index)
                assertTrue(contrastRatio(color, colors.background) >= ContrastTargets.Text)
                assertTrue(contrastRatio(color, colors.surface) >= ContrastTargets.Text)
            }
        }
    }

    @Test
    fun distinctColorsDifferFromEachOther() {
        val colors = tabotaboColors(Hue(90f), isDark = false)
        assertNotEquals(colors.distinct(1), colors.distinct(2))
    }

    @Test
    fun distinctRejectsNegativeIndices() {
        assertFailsWith<IllegalArgumentException> { tabotaboColors(Hue(0f), true).distinct(-1) }
    }
}

class ColorSchemeTest {

    @Test
    fun schemeMapsEveryRole() {
        val colors = tabotaboColors(Hue(140f), isDark = true)
        val scheme = colors.toColorScheme()
        assertEquals(colors.primary, scheme.primary)
        assertEquals(colors.primary, scheme.secondary)
        assertEquals(colors.primary, scheme.tertiary)
        assertEquals(colors.onPrimary, scheme.onPrimary)
        assertEquals(colors.background, scheme.background)
        assertEquals(colors.accent, scheme.onBackground)
        assertEquals(colors.surface, scheme.surface)
        assertEquals(colors.surface, scheme.surfaceContainer)
        assertEquals(colors.surface, scheme.surfaceContainerHighest)
        assertEquals(colors.accent, scheme.onSurface)
        assertEquals(colors.muted, scheme.onSurfaceVariant)
        assertEquals(colors.outline, scheme.outline)
        assertEquals(colors.outline, scheme.outlineVariant)
    }

    @Test
    fun surfaceTintIsTransparent() {
        assertEquals(Color.Transparent, tabotaboLightColorScheme(Hue(0f)).surfaceTint)
        assertEquals(Color.Transparent, tabotaboDarkColorScheme(Hue(0f)).surfaceTint)
    }

    @Test
    fun darkAndLightHelpersMatchTheirPalettes() {
        assertEquals(tabotaboColors(Hue(77f), true).background, tabotaboDarkColorScheme(Hue(77f)).background)
        assertEquals(tabotaboColors(Hue(77f), false).background, tabotaboLightColorScheme(Hue(77f)).background)
    }

    @Test
    fun randomSchemesAreDeterministicForASeed() {
        assertEquals(randomDarkColorScheme(Random(9)).primary, randomDarkColorScheme(Random(9)).primary)
        assertEquals(randomLightColorScheme(Random(9)).primary, randomLightColorScheme(Random(9)).primary)
    }

    @Test
    fun randomSchemesUseTheMatchingMode() {
        val hue = Hue.random(Random(11))
        assertEquals(tabotaboDarkColorScheme(hue).background, randomDarkColorScheme(Random(11)).background)
        assertEquals(tabotaboLightColorScheme(hue).background, randomLightColorScheme(Random(11)).background)
    }
}
