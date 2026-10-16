package com.tabotabo.ui.typography

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class TypographyTest {

    private val typography = tabotaboTypography(FontFamily.Serif, FontFamily.SansSerif, FontFamily.Monospace)

    @Test
    fun rolesHaveTheDesignSystemSizes() {
        assertEquals(42.sp, typography.heroTitle.fontSize)
        assertEquals(34.sp, typography.headline.fontSize)
        assertEquals(32.sp, typography.screenTitle.fontSize)
        assertEquals(16.sp, typography.buttonLabel.fontSize)
        assertEquals(14.sp, typography.body.fontSize)
        assertEquals(14.sp, typography.factTitle.fontSize)
        assertEquals(13.sp, typography.caption.fontSize)
        assertEquals(13.sp, typography.label.fontSize)
        assertEquals(11.sp, typography.footnote.fontSize)
    }

    @Test
    fun rolesHaveTheDesignSystemWeights() {
        assertEquals(FontWeight.Light, typography.screenTitle.fontWeight)
        assertEquals(FontWeight.Normal, typography.headline.fontWeight)
        assertEquals(FontWeight.Normal, typography.body.fontWeight)
        assertEquals(FontWeight.Medium, typography.factTitle.fontWeight)
        assertEquals(FontWeight.Bold, typography.label.fontWeight)
        assertEquals(FontWeight.Bold, typography.buttonLabel.fontWeight)
        assertEquals(FontWeight.Normal, typography.footnote.fontWeight)
    }

    @Test
    fun eachGroupUsesItsOwnFamily() {
        assertEquals(FontFamily.Serif, typography.heroTitle.fontFamily)
        assertEquals(FontFamily.Serif, typography.headline.fontFamily)
        assertEquals(FontFamily.Serif, typography.screenTitle.fontFamily)
        assertEquals(FontFamily.SansSerif, typography.body.fontFamily)
        assertEquals(FontFamily.SansSerif, typography.caption.fontFamily)
        assertEquals(FontFamily.SansSerif, typography.footnote.fontFamily)
        assertEquals(FontFamily.Monospace, typography.label.fontFamily)
        assertEquals(FontFamily.Monospace, typography.buttonLabel.fontFamily)
    }

    @Test
    fun familiesDefaultToTheDisplayFamily() {
        val single = tabotaboTypography(FontFamily.Cursive)
        assertEquals(FontFamily.Cursive, single.body.fontFamily)
        assertEquals(FontFamily.Cursive, single.buttonLabel.fontFamily)
        assertEquals(FontFamily.Cursive, single.bodyLarge.fontFamily)
    }

    @Test
    fun headlinesBalanceTheirLineBreaks() {
        assertEquals(LineBreak.Heading, typography.headline.lineBreak)
        assertEquals(LineBreak.Heading, typography.heroTitle.lineBreak)
        assertEquals(LineBreak.Heading, typography.screenTitle.lineBreak)
    }

    @Test
    fun emphasisWeightPresets() {
        assertEquals(EmphasisWeights(FontWeight.Normal, FontWeight.Bold), EmphasisWeights.Headline)
        assertEquals(EmphasisWeights(FontWeight.Light, FontWeight.Bold), EmphasisWeights.Title)
        assertEquals(EmphasisWeights.Headline, EmphasisWeights())
    }
}
