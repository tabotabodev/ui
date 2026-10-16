package com.tabotabo.ui.text

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.EmphasisWeights
import com.tabotabo.ui.typography.headline

/**
 * Non-animated emphasis line: baseline words in [weights]`.unfocused` and [baseColor], key
 * phrases in [weights]`.focused` and [focusedColor], rendered as one text so it wraps naturally.
 * A line whose only segment is [EmphasisTextScope.FocusedText] renders fully emphasised (e.g.
 * "Settings").
 */
@Composable
public fun EmphasisText(
    modifier: Modifier = Modifier,
    style: TextStyle = TabotaboTheme.typography.headline,
    weights: EmphasisWeights = EmphasisWeights.Headline,
    baseColor: Color = TabotaboTheme.colors.accent,
    focusedColor: Color = TabotaboTheme.colors.primary,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    content: EmphasisTextScope.() -> Unit,
) {
    val line = buildEmphasisLine(content)
    val text = remember(line, weights, focusedColor) { line.toAnnotatedString(weights, focusedColor) }
    BasicText(
        text = text,
        modifier = modifier,
        style = style.emphasisBase(weights, baseColor, textAlign),
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
        minLines = minLines,
    )
}

/**
 * Emphasis line that types in letter by letter, sequentially across segments ("Hello" finishes
 * before "User" starts). It runs once per text per [state]: recomposition and configuration
 * changes don't replay it, while changing the text (or the state's key) does.
 *
 * The full text is laid out from the first frame and only the revealed part is drawn, so nothing
 * reflows while it types and accessibility always sees the full text.
 *
 * @param minLines Reserve lines up front so a changing status headline never shifts layout.
 */
@Composable
public fun TypewriterText(
    modifier: Modifier = Modifier,
    state: TypewriterState = rememberTypewriterState(),
    style: TextStyle = TabotaboTheme.typography.headline,
    weights: EmphasisWeights = EmphasisWeights.Headline,
    baseColor: Color = TabotaboTheme.colors.accent,
    focusedColor: Color = TabotaboTheme.colors.primary,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    content: EmphasisTextScope.() -> Unit,
) {
    TypewriterTextImpl(modifier, state, style, weights, baseColor, focusedColor, textAlign, maxLines, minLines, content = content)
}

/**
 * [TypewriterText] with a layout callback, so tests can assert that typing never re-measures.
 */
@Composable
internal fun TypewriterTextImpl(
    modifier: Modifier,
    state: TypewriterState,
    style: TextStyle,
    weights: EmphasisWeights,
    baseColor: Color,
    focusedColor: Color,
    textAlign: TextAlign,
    maxLines: Int,
    minLines: Int,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    content: EmphasisTextScope.() -> Unit,
) {
    val line = buildEmphasisLine(content)
    val text = remember(line, weights, focusedColor) { line.toAnnotatedString(weights, focusedColor) }
    val animate = TabotaboTheme.motion.enabled
    LaunchedEffect(state, line.text, state.restartToken, animate) { state.reveal(line.text, animate) }

    val reveal = remember { RevealClip() }
    BasicText(
        text = text,
        modifier = modifier.drawWithContent {
            // Read only here: advancing the reveal invalidates draw, not composition or layout.
            val count = state.revealedChars
            val layout = reveal.layout
            when {
                layout == null || count >= text.length -> drawContent()
                count > 0 -> clipPath(reveal.pathFor(layout, count)) { this@drawWithContent.drawContent() }
            }
        },
        style = style.emphasisBase(weights, baseColor, textAlign),
        onTextLayout = {
            reveal.layout = it
            onTextLayout(it)
        },
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
        minLines = minLines,
    )
}

/** Base style of an emphasis line: [baseColor] in the unfocused weight, aligned by [textAlign]. */
private fun TextStyle.emphasisBase(weights: EmphasisWeights, baseColor: Color, textAlign: TextAlign) =
    merge(TextStyle(color = baseColor, fontWeight = weights.unfocused, textAlign = textAlign))

/** Holds the latest layout and caches the clip path for the last revealed count. */
private class RevealClip {
    var layout: TextLayoutResult? = null
    private var cachedLayout: TextLayoutResult? = null
    private var cachedCount = -1
    private var cachedPath: Path = Path()

    /** The outline of characters `[0, count)` in [layout], recomputed only when either changes. */
    fun pathFor(layout: TextLayoutResult, count: Int): Path {
        if (layout !== cachedLayout || count != cachedCount) {
            cachedPath = layout.getPathForRange(0, count)
            cachedLayout = layout
            cachedCount = count
        }
        return cachedPath
    }
}
