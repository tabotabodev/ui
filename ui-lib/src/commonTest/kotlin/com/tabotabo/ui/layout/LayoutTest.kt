package com.tabotabo.ui.layout

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class DimensionsTest {

    @Test
    fun compactUsesTheBaseSpacingScale() {
        assertEquals(Spacing(4.dp, 8.dp, 12.dp, 16.dp, 24.dp), TabotaboDimensions.default(SizeClass.Compact).spacing)
    }

    @Test
    fun expandedScalesSpacingByOneAndAHalf() {
        assertEquals(Spacing(6.dp, 12.dp, 18.dp, 24.dp, 36.dp), TabotaboDimensions.default(SizeClass.Expanded).spacing)
    }

    @Test
    fun expandedKeepsStrokeAndShapeTokens() {
        val compact = TabotaboDimensions.default(SizeClass.Compact)
        val expanded = TabotaboDimensions.default(SizeClass.Expanded)
        assertEquals(compact.cardCorner, expanded.cardCorner)
        assertEquals(compact.outlineWidth, expanded.outlineWidth)
    }

    @Test
    fun sizeClassSwitchesAt600dp() {
        assertEquals(SizeClass.Compact, sizeClassOf(599.dp))
        assertEquals(SizeClass.Expanded, sizeClassOf(600.dp))
    }

    @Test
    fun marginsAreAFractionOfTheShorterSide() {
        val dimensions = TabotaboDimensions()
        assertEquals(32f, dimensions.horizontalMargin(400.dp, isLandscape = false).value, 0.01f)
        assertEquals(56f, dimensions.horizontalMargin(400.dp, isLandscape = true).value, 0.01f)
    }

    @Test
    fun webPaddingIsClamped() {
        assertEquals(20.dp, webHorizontalPadding(200.dp))
        assertEquals(60f, webHorizontalPadding(1000.dp).value, 0.01f)
        assertEquals(96.dp, webHorizontalPadding(3000.dp))
    }
}

class CardListTest {

    @Test
    fun gridFitsAsManyMinimumCellsAsPossible() {
        assertEquals(3, gridColumnCount(width = 900.dp, minCellWidth = 280.dp, gap = 12.dp))
        assertEquals(2, gridColumnCount(width = 600.dp, minCellWidth = 280.dp, gap = 12.dp))
    }

    @Test
    fun gridAccountsForGaps() {
        // Two 280dp cells need 572dp with a 12dp gap.
        assertEquals(1, gridColumnCount(width = 571.dp, minCellWidth = 280.dp, gap = 12.dp))
        assertEquals(2, gridColumnCount(width = 572.dp, minCellWidth = 280.dp, gap = 12.dp))
    }

    @Test
    fun gridAlwaysHasAColumn() {
        assertEquals(1, gridColumnCount(width = 100.dp, minCellWidth = 280.dp, gap = 12.dp))
        assertEquals(1, gridColumnCount(width = 900.dp, minCellWidth = 0.dp, gap = 12.dp))
    }
}
