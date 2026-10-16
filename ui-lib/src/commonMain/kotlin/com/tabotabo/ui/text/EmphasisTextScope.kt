package com.tabotabo.ui.text

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.tabotabo.ui.typography.EmphasisWeights

/** Marks the emphasis-text builder DSL so nested scopes can't leak into each other. */
@DslMarker
public annotation class EmphasisTextDsl

/**
 * Collects the segments of one emphasis line. Calls are not composables; they only record spans
 * that are later rendered as a single text.
 *
 * Spacing is automatic: one space goes between segments unless either side already has
 * whitespace at the boundary, or the next segment starts with punctuation (`?!.,:;`).
 */
@EmphasisTextDsl
public interface EmphasisTextScope {
    /** Appends [text] in the unfocused weight and the baseline (accent) color. */
    public fun UnfocusedText(text: String)

    /** Appends [text] in the focused weight and [color] (primary when unspecified). */
    public fun FocusedText(text: String, color: Color = Color.Unspecified)

    /**
     * Appends a localised format string whose single `%s` placeholder becomes [focused] in the
     * focused style; the rest stays unfocused. Example: `Formatted("Set up the %s", "match")`.
     *
     * @throws IllegalArgumentException when [format] has no `%s` or more than one.
     */
    public fun Formatted(format: String, focused: String)

    /** Overrides the automatic spacing before the next segment with [count] spaces. */
    public fun Space(count: Int = 1)

    /** Suppresses the automatic space before the next segment (e.g. before "?"). */
    public fun NoSpace()
}

/** Placeholder [EmphasisTextScope.Formatted] replaces with the focused phrase. */
private const val PLACEHOLDER = "%s"

/** Characters that never get an automatic space before them. */
private const val NO_SPACE_BEFORE = "?!.,:;"

/**
 * A focused range of an [EmphasisLine].
 *
 * @property start Inclusive start index.
 * @property end Exclusive end index.
 * @property color Color override, or [Color.Unspecified] for the theme's focused color.
 */
@Immutable
internal data class FocusedRange(val start: Int, val end: Int, val color: Color = Color.Unspecified)

/** One built emphasis line: its full [text] and which ranges of it are focused. */
@Immutable
internal data class EmphasisLine(val text: String, val focused: List<FocusedRange>) {

    /**
     * Renders this line as an [AnnotatedString]: focused ranges get [EmphasisWeights.focused] and
     * their color (or [focusedColor]); everything else inherits the surrounding text style.
     */
    fun toAnnotatedString(weights: EmphasisWeights, focusedColor: Color): AnnotatedString = buildAnnotatedString {
        var position = 0
        for (range in focused) {
            append(text.substring(position, range.start))
            withStyle(SpanStyle(fontWeight = weights.focused, color = range.color.takeOrElse { focusedColor })) {
                append(text.substring(range.start, range.end))
            }
            position = range.end
        }
        append(text.substring(position))
    }
}

/** Builds an [EmphasisLine] from the DSL in [content]. */
internal fun buildEmphasisLine(content: EmphasisTextScope.() -> Unit): EmphasisLine =
    EmphasisLineBuilder().apply(content).build()

/** The [EmphasisTextScope] implementation behind [buildEmphasisLine]. */
private class EmphasisLineBuilder : EmphasisTextScope {
    private val text = StringBuilder()
    private val focused = mutableListOf<FocusedRange>()
    private var pendingSpaces: Int? = null

    override fun UnfocusedText(text: String) = append(text, isFocused = false, color = Color.Unspecified)

    override fun FocusedText(text: String, color: Color) = append(text, isFocused = true, color = color)

    override fun Formatted(format: String, focused: String) {
        val index = format.indexOf(PLACEHOLDER)
        require(index >= 0) { "Format \"$format\" has no $PLACEHOLDER placeholder" }
        require(format.indexOf(PLACEHOLDER, index + PLACEHOLDER.length) < 0) {
            "Format \"$format\" has more than one $PLACEHOLDER placeholder"
        }
        val before = format.substring(0, index)
        val after = format.substring(index + PLACEHOLDER.length)
        // Only the first piece gets automatic spacing; the format string spaces its own pieces.
        var first = true
        fun piece(part: String, isFocused: Boolean) {
            if (part.isEmpty()) return
            append(part, isFocused, Color.Unspecified, autoSpace = first)
            first = false
        }
        piece(before, isFocused = false)
        piece(focused, isFocused = true)
        piece(after, isFocused = false)
    }

    override fun Space(count: Int) {
        require(count >= 0) { "count must be non-negative, was $count" }
        pendingSpaces = count
    }

    override fun NoSpace() {
        pendingSpaces = 0
    }

    private fun append(segment: String, isFocused: Boolean, color: Color, autoSpace: Boolean = true) {
        if (segment.isEmpty()) return
        if (text.isNotEmpty()) {
            val spaces = pendingSpaces ?: if (autoSpace && needsAutoSpace(text.last(), segment.first())) 1 else 0
            repeat(spaces) { text.append(' ') }
        }
        pendingSpaces = null
        val start = text.length
        text.append(segment)
        if (isFocused) focused += FocusedRange(start, text.length, color)
    }

    private fun needsAutoSpace(previous: Char, next: Char): Boolean =
        !previous.isWhitespace() && !next.isWhitespace() && next !in NO_SPACE_BEFORE

    fun build(): EmphasisLine = EmphasisLine(text.toString(), mergeAdjacent(focused))

    /** Merges focused ranges that touch and share a color, so spans stay minimal. */
    private fun mergeAdjacent(ranges: List<FocusedRange>): List<FocusedRange> = ranges.fold(mutableListOf()) { merged, range ->
        val last = merged.lastOrNull()
        if (last != null && last.end == range.start && last.color == range.color) {
            merged[merged.lastIndex] = last.copy(end = range.end)
        } else {
            merged += range
        }
        merged
    }
}
