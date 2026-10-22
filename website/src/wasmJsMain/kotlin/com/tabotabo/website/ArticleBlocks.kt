package com.tabotabo.website

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.text.EmphasisText
import com.tabotabo.ui.text.EmphasisTextScope
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.EmphasisWeights

/** Comfortable reading width for article prose. */
private val ReadingWidth = 680.dp

/** Width from which an example shows its code and output side by side. */
private val SideBySideWidth = 760.dp

/** Root of the published Dokka reference, relative to the website. */
private const val API_ROOT = "api/tabotabo-ui/"

/** Collects one paragraph: plain text, bold runs and links, in reading order. */
internal class ParagraphScope(
    private val builder: AnnotatedString.Builder,
    private val linkStyles: TextLinkStyles,
    private val navigate: (SitePage) -> Unit,
) {
    /** Appends plain text. */
    operator fun String.unaryPlus() {
        builder.append(this)
    }

    /** Appends [text] in Bold. */
    fun bold(text: String) {
        builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(text) }
    }

    /** Appends [text] as a link to [url]; relative URLs resolve against the website. */
    fun link(text: String, url: String) {
        builder.withLink(LinkAnnotation.Url(url, linkStyles)) { append(text) }
    }

    /** Appends [text] as a link to another [page] of the site, opened without a page load. */
    fun link(text: String, page: SitePage) {
        builder.withLink(LinkAnnotation.Clickable(page.route, linkStyles) { navigate(page) }) { append(text) }
    }
}

/** A paragraph of article prose at reading width, with inline bold runs and links. */
@Composable
internal fun Paragraph(modifier: Modifier = Modifier, content: ParagraphScope.() -> Unit) {
    val primary = TabotaboTheme.colors.primary
    val linkStyles = TextLinkStyles(
        style = SpanStyle(color = primary, fontWeight = FontWeight.Bold),
        hoveredStyle = SpanStyle(color = primary, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline),
    )
    val navigate = LocalNavigate.current
    val text = buildAnnotatedString { ParagraphScope(this, linkStyles, navigate).content() }
    Text(
        text = text,
        modifier = modifier.widthIn(max = ReadingWidth).padding(top = TabotaboTheme.dimensions.spacing.m),
        color = TabotaboTheme.colors.accent,
        style = TabotaboTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
    )
}

/** A smaller heading inside a section, followed by its [content]. */
@Composable
internal fun SubSection(title: EmphasisTextScope.() -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 40.dp)) {
        EmphasisText(style = TabotaboTheme.typography.headlineSmall, weights = EmphasisWeights.Title, content = title)
        content()
    }
}

/** Small uppercase label above a heading or a panel. */
@Composable
internal fun Eyebrow(text: String) {
    Text(
        text = text.uppercase(),
        color = TabotaboTheme.colors.muted,
        style = TabotaboTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
    )
}

/**
 * A short code hint in an outlined panel. Set in Montserrat like everything else; long lines
 * scroll sideways instead of wrapping.
 */
@Composable
internal fun CodeHint(code: String, modifier: Modifier = Modifier) {
    val colors = TabotaboTheme.colors
    val dimensions = TabotaboTheme.dimensions
    val shape = RoundedCornerShape(dimensions.cardCorner)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface)
            .border(dimensions.outlineWidth, colors.outline, shape)
            .horizontalScroll(rememberScrollState())
            .padding(dimensions.spacing.l),
    ) {
        Text(
            text = code.trimIndent(),
            color = colors.accent,
            style = TabotaboTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, lineHeight = 21.sp),
            softWrap = false,
        )
    }
}

/**
 * A code hint beside the live [output] it produces (stacked on narrow screens), followed by a
 * link to the API reference page at [apiPath] (relative to the module root of the Dokka site).
 */
@Composable
internal fun Example(
    code: String,
    apiPath: String,
    apiLabel: String = "API reference",
    output: @Composable ColumnScope.() -> Unit,
) {
    val spacing = TabotaboTheme.dimensions.spacing
    Column(Modifier.fillMaxWidth().padding(top = spacing.l)) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            if (maxWidth >= SideBySideWidth) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.m)) {
                    LabeledPanel("Code", Modifier.weight(1f)) { CodeHint(code) }
                    LabeledPanel("Output", Modifier.weight(1f)) { OutputCard(output) }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.l)) {
                    LabeledPanel("Code") { CodeHint(code) }
                    LabeledPanel("Output") { OutputCard(output) }
                }
            }
        }
        Paragraph { link(apiLabel, API_ROOT + apiPath) }
    }
}

/** An [Eyebrow] label above [content]. */
@Composable
private fun LabeledPanel(label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier) {
        Eyebrow(label)
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.s))
        content()
    }
}

/** The outlined card a live example renders into. */
@Composable
private fun OutputCard(content: @Composable ColumnScope.() -> Unit) {
    GlowCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m), content = content)
    }
}

/**
 * A table that never scrolls the page sideways: columns when wide; on a phone each row becomes a
 * card titled by its first cell, with the other cells as label / value pairs.
 *
 * @param headers Column titles; the first names the row titles.
 * @param rows Cells per row, one per header.
 * @param weights Relative column widths when wide; equal by default.
 */
@Composable
internal fun SpecTable(
    headers: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier,
    weights: List<Float> = headers.map { 1f },
) {
    val colors = TabotaboTheme.colors
    val typography = TabotaboTheme.typography
    val spacing = TabotaboTheme.dimensions.spacing
    if (LocalPhoneLayout.current) {
        Column(modifier.fillMaxWidth().padding(top = spacing.l), verticalArrangement = Arrangement.spacedBy(spacing.s)) {
            rows.forEach { row ->
                GlowCard(Modifier.fillMaxWidth()) {
                    Text(row.first(), color = colors.accent, style = typography.labelLarge)
                    row.drop(1).forEachIndexed { index, cell ->
                        Spacer(Modifier.height(spacing.s))
                        Text(headers[index + 1], color = colors.muted, style = typography.labelMedium)
                        Text(cell, color = colors.accent, style = typography.bodyMedium)
                    }
                }
            }
        }
    } else {
        val divider = @Composable {
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.outline))
        }
        Column(modifier.fillMaxWidth().padding(top = spacing.l)) {
            Row(Modifier.fillMaxWidth().padding(vertical = spacing.s), horizontalArrangement = Arrangement.spacedBy(spacing.l)) {
                headers.forEachIndexed { index, header ->
                    Text(header, Modifier.weight(weights[index]), color = colors.muted, style = typography.labelMedium)
                }
            }
            divider()
            rows.forEach { row ->
                Row(Modifier.fillMaxWidth().padding(vertical = spacing.m), horizontalArrangement = Arrangement.spacedBy(spacing.l)) {
                    row.forEachIndexed { index, cell ->
                        Text(
                            text = cell,
                            modifier = Modifier.weight(weights[index]),
                            color = colors.accent,
                            style = if (index == 0) typography.labelLarge else typography.bodyMedium,
                        )
                    }
                }
                divider()
            }
        }
    }
}
