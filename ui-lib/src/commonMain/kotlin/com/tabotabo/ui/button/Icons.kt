package com.tabotabo.ui.button

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

/** Stroke width of drawn icons, as a fraction of their size. */
private const val ICON_STROKE_FRACTION = 0.12f

/**
 * A back arrow drawn as simple vector strokes in `LocalContentColor`, centred by its drawn
 * bounds rather than font metrics. Decorative: the enclosing [IconAction] carries the label.
 */
@Composable
internal fun BackArrowIcon(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier = modifier.size(24.dp)) {
        val stroke = size.minDimension * ICON_STROKE_FRACTION
        val midY = size.height / 2
        val tip = Offset(stroke / 2, midY)
        val head = size.width * 0.45f
        drawLine(color, tip, Offset(size.width - stroke / 2, midY), stroke, StrokeCap.Round)
        drawLine(color, tip, Offset(head, midY - head + stroke / 2), stroke, StrokeCap.Round)
        drawLine(color, tip, Offset(head, midY + head - stroke / 2), stroke, StrokeCap.Round)
    }
}
