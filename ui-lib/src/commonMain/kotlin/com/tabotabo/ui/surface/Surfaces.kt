package com.tabotabo.ui.surface

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tabotabo.ui.text.EmphasisText
import com.tabotabo.ui.text.EmphasisTextScope
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.EmphasisWeights
import com.tabotabo.ui.typography.buttonLabel
import com.tabotabo.ui.typography.footnote
import com.tabotabo.ui.typography.label

/** Fully rounded ends for pills and tags. */
private val PillShape = RoundedCornerShape(percent = 50)

/**
 * Outlined card: the theme's `surface` fill with a thin outline and no resting shadow. When
 * [highlighted], the outline animates to [highlightColor] at the highlight width and, if [glow]
 * is set, a colored shadow fades in. Use glow sparingly, for the one thing that needs attention.
 *
 * @param onClick Non-null makes the card clickable (with ripple and button semantics).
 */
@Composable
public fun GlowCard(
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    highlightColor: Color = TabotaboTheme.colors.primary,
    glow: Boolean = highlighted,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(TabotaboTheme.dimensions.cardCorner),
    contentPadding: PaddingValues = PaddingValues(TabotaboTheme.dimensions.spacing.l),
    content: @Composable ColumnScope.() -> Unit,
) {
    val dimensions = TabotaboTheme.dimensions
    val millis = TabotaboTheme.motion.highlightMillis
    val outlineColor by animateColorAsState(
        if (highlighted) highlightColor else TabotaboTheme.colors.outline, tween(millis), label = "cardOutline",
    )
    val outlineWidth by animateDpAsState(
        if (highlighted) dimensions.highlightOutlineWidth else dimensions.outlineWidth, tween(millis), label = "cardOutlineWidth",
    )
    val glowRadius by animateDpAsState(
        if (highlighted && glow) dimensions.glowRadius else 0.dp, tween(millis), label = "cardGlow",
    )

    Column(
        modifier = modifier
            .glowOutline(outlineColor, outlineWidth, shape, glowRadius, fill = TabotaboTheme.colors.surface)
            .then(
                if (onClick != null) Modifier.clickable(enabled = enabled, role = Role.Button, onClick = onClick) else Modifier,
            )
            .padding(contentPadding),
        content = content,
    )
}

/**
 * The outline / glow drawing as a reusable modifier, for custom shapes: an optional colored
 * shadow of [glowRadius], then [shape] clipped and outlined in [color] at [width].
 */
public fun Modifier.glowOutline(
    color: Color,
    width: Dp,
    shape: Shape,
    glowRadius: Dp = 0.dp,
): Modifier = glowOutline(color, width, shape, glowRadius, fill = Color.Unspecified)

/** [glowOutline] that also fills the clipped [shape] with [fill] (skipped when unspecified). */
internal fun Modifier.glowOutline(color: Color, width: Dp, shape: Shape, glowRadius: Dp, fill: Color): Modifier =
    this
        .then(if (glowRadius > 0.dp) Modifier.shadow(glowRadius, shape, ambientColor = color, spotColor = color) else Modifier)
        .clip(shape)
        .then(if (fill != Color.Unspecified) Modifier.background(fill) else Modifier)
        .border(width, color, shape)

/** One pill in a 2–3 way choice: selected = primary outline and label, no glow. */
@Composable
public fun SelectablePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = TabotaboTheme.colors
    val dimensions = TabotaboTheme.dimensions
    val millis = TabotaboTheme.motion.highlightMillis
    val outline by animateColorAsState(if (selected) colors.primary else colors.outline, tween(millis), label = "pillOutline")
    val labelColor by animateColorAsState(
        when {
            !enabled -> colors.disabled
            selected -> colors.primary
            else -> colors.muted
        },
        tween(millis),
        label = "pillLabel",
    )
    Box(
        modifier = modifier
            .glowOutline(
                outline,
                if (selected) dimensions.highlightOutlineWidth else dimensions.outlineWidth,
                PillShape,
                fill = colors.surface,
                glowRadius = 0.dp,
            )
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = dimensions.spacing.l, vertical = dimensions.spacing.m),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = labelColor,
            style = TabotaboTheme.typography.label,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
        )
    }
}

/** Row of equal-width [SelectablePill]s with radio-group semantics. */
@Composable
public fun <T> SelectablePillRow(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s),
    ) {
        options.forEach { option ->
            SelectablePill(
                text = label(option),
                selected = option == selected,
                onClick = { onSelect(option) },
                modifier = Modifier.weight(1f),
                enabled = enabled,
            )
        }
    }
}

/** Tiny display-only outlined tag ("CPU", "New") with a bold footnote label. Not clickable. */
@Composable
public fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TabotaboTheme.colors.muted,
) {
    val spacing = TabotaboTheme.dimensions.spacing
    Box(
        modifier = modifier
            .border(TabotaboTheme.dimensions.outlineWidth, color, PillShape)
            .padding(horizontal = spacing.s, vertical = spacing.xs / 2),
    ) {
        Text(text = text, color = color, style = TabotaboTheme.typography.footnote.copy(fontWeight = FontWeight.Bold), maxLines = 1)
    }
}

/**
 * Outlined dialog with a left-aligned emphasis [title] and primary-colored text actions.
 *
 * @param dismissText Label of the optional dismiss action; omitted when null.
 * @param onDismiss Called by the dismiss action; defaults to [onDismissRequest].
 */
@Composable
public fun TabotaboDialog(
    onDismissRequest: () -> Unit,
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
    onDismiss: () -> Unit = onDismissRequest,
    title: EmphasisTextScope.() -> Unit,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val dimensions = TabotaboTheme.dimensions
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .glowOutline(
                    TabotaboTheme.colors.outline,
                    dimensions.outlineWidth,
                    RoundedCornerShape(dimensions.cardCorner),
                    fill = TabotaboTheme.colors.surface,
                    glowRadius = 0.dp,
                )
                .padding(dimensions.spacing.xl),
        ) {
            EmphasisText(style = TabotaboTheme.typography.headlineSmall, weights = EmphasisWeights.Title, content = title)
            if (content != null) {
                Spacer(Modifier.height(dimensions.spacing.m))
                content()
            }
            Spacer(Modifier.height(dimensions.spacing.xl))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.spacing.s, Alignment.End),
            ) {
                if (dismissText != null) DialogAction(dismissText, onDismiss)
                DialogAction(confirmText, onConfirm)
            }
        }
    }
}

/** A primary-colored text action of a [TabotaboDialog]. */
@Composable
private fun DialogAction(text: String, onClick: () -> Unit) {
    val spacing = TabotaboTheme.dimensions.spacing
    Text(
        text = text,
        color = TabotaboTheme.colors.primary,
        style = TabotaboTheme.typography.buttonLabel,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(TabotaboTheme.dimensions.cardCorner))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = spacing.m, vertical = spacing.s),
    )
}
