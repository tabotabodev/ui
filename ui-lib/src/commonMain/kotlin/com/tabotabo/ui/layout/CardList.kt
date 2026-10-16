package com.tabotabo.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
 * - portrait and landscape: a vertically scrolling lazy column, so in landscape the content pane
 *   scrolls on its own, independently of the header/actions pane;
 * - web layout: an `auto-fill`-style grid whose cells are at least [minCellWidth] wide. It is not
 *   lazy, so it can live inside the web layout's scrolling column.
 *
 * @param key Stable key per item for the lazy column.
 */
@Composable
public fun <T> CardList(
    items: List<T>,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    minCellWidth: Dp = 280.dp,
    itemContent: @Composable (T) -> Unit,
) {
    val gap = TabotaboTheme.dimensions.spacing.m
    if (LocalScreenLayoutInfo.current.isWebLayout) {
        WebGrid(items, modifier, minCellWidth, gap, itemContent)
    } else {
        LazyColumn(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(gap)) {
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
