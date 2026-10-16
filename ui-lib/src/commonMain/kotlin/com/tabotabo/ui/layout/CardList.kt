package com.tabotabo.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.theme.TabotaboTheme

/**
 * Number of grid columns of at least [minCellWidth] that fit in [width] with [gap] between them;
 * always at least one.
 */
internal fun gridColumnCount(width: Dp, minCellWidth: Dp, gap: Dp): Int {
    if (minCellWidth <= 0.dp) return 1
    return ((width + gap) / (minCellWidth + gap)).toInt().coerceAtLeast(1)
}

/**
 * Card list that adapts to the enclosing [ScreenScaffold] (via [LocalScreenLayoutInfo]):
 * - portrait: a vertical lazy column;
 * - landscape: horizontally scrolling columns of [cardsPerColumn] cards, also vertically
 *   scrollable for short screens;
 * - web layout: an `auto-fill`-style grid whose cells are at least [minCellWidth] wide. It is not
 *   lazy, so it can live inside the web layout's scrolling column.
 *
 * @param key Stable key per item for lazy layouts; the landscape columns use their first item's key.
 */
@Composable
public fun <T> CardList(
    items: List<T>,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    cardsPerColumn: Int = 3,
    minCellWidth: Dp = 280.dp,
    itemContent: @Composable (T) -> Unit,
) {
    require(cardsPerColumn > 0) { "cardsPerColumn must be positive, was $cardsPerColumn" }
    val gap = TabotaboTheme.dimensions.spacing.m
    val info = LocalScreenLayoutInfo.current
    when {
        info.isWebLayout -> WebGrid(items, modifier, minCellWidth, gap, itemContent)
        info.isLandscape -> {
            val columns = items.chunked(cardsPerColumn)
            LazyRow(
                modifier = modifier.verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                items(columns, key = key?.let { itemKey -> { column -> itemKey(column.first()) } }) { column ->
                    Column(modifier = Modifier.width(minCellWidth), verticalArrangement = Arrangement.spacedBy(gap)) {
                        column.forEach { itemContent(it) }
                    }
                }
            }
        }
        else -> LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(gap)) {
            items(items, key = key) { itemContent(it) }
        }
    }
}

/** The non-lazy responsive grid behind the web layout of [CardList]. */
@Composable
private fun <T> WebGrid(
    items: List<T>,
    modifier: Modifier,
    minCellWidth: Dp,
    gap: Dp,
    itemContent: @Composable (T) -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columnCount = gridColumnCount(maxWidth, minCellWidth, gap)
        Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            items.chunked(columnCount).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    row.forEach { item -> Box(Modifier.weight(1f)) { itemContent(item) } }
                    // Keep cells in a short last row the same width as the rest.
                    repeat(columnCount - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
