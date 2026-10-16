package com.tabotabo.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.button.BackArrowIcon
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.text.EmphasisTextScope
import com.tabotabo.ui.text.ScreenTitle
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme

/** Share of the width the landscape header/actions pane takes. */
private const val LANDSCAPE_HEADER_PANE_WEIGHT = 0.45f

/** Web side padding: a fraction of the viewport width, clamped to [WebMinPadding]..[WebMaxPadding]. */
private const val WEB_PADDING_FRACTION = 0.06f
private val WebMinPadding = 20.dp
private val WebMaxPadding = 96.dp

/** Width cap of stacked action buttons in the web layout. */
private val WebActionsMaxWidth = 480.dp

/** Side margin for a space whose shorter side is [shorterSide]. */
internal fun TabotaboDimensions.horizontalMargin(shorterSide: Dp, isLandscape: Boolean): Dp =
    shorterSide * if (isLandscape) landscapeMarginFraction else portraitMarginFraction

/** Web side padding for a viewport [width] wide. */
internal fun webHorizontalPadding(width: Dp): Dp = (width * WEB_PADDING_FRACTION).coerceIn(WebMinPadding, WebMaxPadding)

/**
 * Screen skeleton driven by its own constraints, with no platform window APIs.
 * - Portrait: [header] on top, [content] in the middle (centred between them), [actions] pinned
 *   to the bottom.
 * - Landscape: [content] on the left; [header] + [actions] stacked on the right, vertically
 *   centred and scrollable on overflow.
 * Side margins follow the [TabotaboDimensions] fractions; safe-drawing insets are applied, and
 * [LocalScreenLayoutInfo] is provided to every slot.
 *
 * @param webLayout When true, uses the single-column web layout (centred column capped at
 *   [webMaxWidth], header → content → actions, all scrolling) instead of the panes.
 */
@Composable
public fun ScreenScaffold(
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actions: (@Composable ColumnScope.() -> Unit)? = null,
    webLayout: Boolean = false,
    webMaxWidth: Dp = 1080.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val dimensions = TabotaboTheme.dimensions
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(TabotaboTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        val isLandscape = maxWidth > maxHeight
        val shorterSide = minOf(maxWidth, maxHeight)
        val margin = if (webLayout) webHorizontalPadding(maxWidth) else dimensions.horizontalMargin(shorterSide, isLandscape)
        val info = ScreenLayoutInfo(isLandscape, sizeClassOf(shorterSide), margin, isWebLayout = webLayout)

        CompositionLocalProvider(LocalScreenLayoutInfo provides info) {
            when {
                webLayout -> WebLayout(header, actions, webMaxWidth, margin, content)
                isLandscape -> LandscapeLayout(header, actions, margin, shorterSide, content)
                else -> PortraitLayout(header, actions, margin, content)
            }
        }
    }
}

/** Portrait: header, centred content taking the remaining height, actions at the bottom. */
@Composable
private fun PortraitLayout(
    header: @Composable () -> Unit,
    actions: (@Composable ColumnScope.() -> Unit)?,
    margin: Dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val spacing = TabotaboTheme.dimensions.spacing
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = margin, vertical = spacing.l)) {
        header()
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = spacing.l),
            contentAlignment = Alignment.Center,
            content = content,
        )
        if (actions != null) ActionStack(content = actions)
    }
}

/** Landscape: content pane on the left; header and actions, centred and scrollable, on the right. */
@Composable
private fun LandscapeLayout(
    header: @Composable () -> Unit,
    actions: (@Composable ColumnScope.() -> Unit)?,
    margin: Dp,
    shorterSide: Dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val dimensions = TabotaboTheme.dimensions
    val paneGap = shorterSide * dimensions.portraitMarginFraction
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = margin, vertical = dimensions.spacing.l),
        horizontalArrangement = Arrangement.spacedBy(paneGap),
    ) {
        Box(
            modifier = Modifier.weight(1f - LANDSCAPE_HEADER_PANE_WEIGHT).fillMaxHeight(),
            contentAlignment = Alignment.Center,
            content = content,
        )
        BoxWithConstraints(modifier = Modifier.weight(LANDSCAPE_HEADER_PANE_WEIGHT).fillMaxHeight()) {
            val paneHeight = maxHeight
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(min = paneHeight),
                    verticalArrangement = Arrangement.Center,
                ) {
                    header()
                    if (actions != null) {
                        Spacer(Modifier.height(dimensions.spacing.xl))
                        ActionStack(content = actions)
                    }
                }
            }
        }
    }
}

/** Web: one centred, scrolling column — header, content, then width-capped actions. */
@Composable
private fun WebLayout(
    header: @Composable () -> Unit,
    actions: (@Composable ColumnScope.() -> Unit)?,
    maxWidth: Dp,
    padding: Dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val spacing = TabotaboTheme.dimensions.spacing
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.widthIn(max = maxWidth).fillMaxWidth().padding(horizontal = padding, vertical = spacing.xl),
        ) {
            header()
            Spacer(Modifier.height(spacing.xl))
            Box(modifier = Modifier.fillMaxWidth(), content = content)
            if (actions != null) {
                Spacer(Modifier.height(spacing.xl))
                ActionStack(maxWidth = WebActionsMaxWidth, content = actions)
            }
        }
    }
}

/**
 * Header: an optional bare back arrow beside the title, the emphasis [title], a muted
 * [subtitle] directly under it, and optional trailing icon [actions]. [subtitleMinLines]
 * reserves space so the header never shifts.
 */
@Composable
public fun ScreenHeader(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    backContentDescription: String? = null,
    subtitle: String? = null,
    subtitleMinLines: Int = 1,
    animateTitle: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    title: EmphasisTextScope.() -> Unit,
) {
    val spacing = TabotaboTheme.dimensions.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconAction(onClick = onBack, contentDescription = backContentDescription) { BackArrowIcon() }
                Spacer(Modifier.width(spacing.xs))
            }
            ScreenTitle(modifier = Modifier.weight(1f), animate = animateTitle, content = title)
            actions()
        }
        if (subtitle != null) {
            Spacer(Modifier.height(spacing.xs))
            Subtitle(text = subtitle, minLines = subtitleMinLines, maxLines = maxOf(subtitleMinLines, 2))
        }
    }
}
