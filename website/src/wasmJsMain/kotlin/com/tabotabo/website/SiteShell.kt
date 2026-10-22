package com.tabotabo.website

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.layout.ScreenHeader
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.surface.TabotaboDialog
import com.tabotabo.ui.text.EmphasisText
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.EmphasisWeights
import com.tabotabo.ui.typography.montserratTypography
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Below this window width the site uses its phone layout: menu button and phone type scale. */
internal val PhoneMaxWidth: Dp = 600.dp

/** Height of the site header row. */
private val HeaderHeight = 56.dp

/** Minimum size of anything tappable. */
private val TouchTarget = 48.dp

internal const val GITHUB_URL = "https://github.com/tabotabodev/ui"
internal const val API_URL = "api/"
internal const val SHOWCASE_URL = "showcase/"

/** Links to the parts of the site outside the app, shown in the footer. */
private val ExternalLinks = listOf("API" to API_URL, "Showcase" to SHOWCASE_URL, "GitHub" to GITHUB_URL)

/**
 * The library's Montserrat typography, with display and headline roles scaled down on a [phone]
 * so a title fits in two or three lines. Body and label roles keep their sizes.
 */
@Composable
internal fun siteTypography(phone: Boolean): Typography {
    val base = montserratTypography()
    if (!phone) return base
    fun TextStyle.sized(size: Int, lineHeight: Int) = copy(fontSize = size.sp, lineHeight = lineHeight.sp)
    return base.copy(
        displayLarge = base.displayLarge.sized(34, 42),
        displayMedium = base.displayMedium.sized(28, 36),
        headlineLarge = base.headlineLarge.sized(28, 36),
        headlineMedium = base.headlineMedium.sized(24, 32),
        headlineSmall = base.headlineSmall.sized(22, 28),
    )
}

/** Widest the page column gets, as in the library's web layout. */
private val ColumnMaxWidth = 1080.dp

/** Side padding of the page column for a window [width] wide: 6% of it, 20–96dp (the library's web layout). */
private fun columnPadding(width: Dp): Dp = (width * 0.06f).coerceIn(20.dp, 96.dp)

/**
 * The frame every page shares: the site header pinned to the top, then a scrolling column with a
 * breadcrumb for sub-pages, the page [heading] (defaults to the page's title and subtitle),
 * [content] and the footer. The column is the library's single-column web layout; the header is
 * lined up with it.
 */
@Composable
internal fun SitePageScaffold(
    page: SitePage,
    heading: @Composable () -> Unit = { PageHeading(page) },
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = TabotaboTheme.dimensions.spacing
    Column(Modifier.fillMaxSize().background(TabotaboTheme.colors.background)) {
        PinnedHeader(page)
        ScreenScaffold(
            modifier = Modifier.weight(1f),
            webLayout = true,
            header = {
                Column(Modifier.fillMaxWidth()) {
                    page.parent?.let { parent ->
                        Breadcrumb(parent = parent, current = page)
                        Spacer(Modifier.height(spacing.s))
                    }
                    heading()
                }
            },
        ) {
            Column(Modifier.fillMaxWidth()) {
                // Lets readers select and copy page text, code samples included.
                SelectionContainer { Column(Modifier.fillMaxWidth(), content = content) }
                SiteFooter()
            }
        }
    }
}

/** The [SiteHeader], fixed above the scrolling page, aligned with the page column, with a divider under it. */
@Composable
private fun PinnedHeader(page: SitePage) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val padding = columnPadding(maxWidth)
        Column(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Box(Modifier.widthIn(max = ColumnMaxWidth).fillMaxWidth().padding(horizontal = padding, vertical = TabotaboTheme.dimensions.spacing.xs)) {
                    SiteHeader(current = page)
                }
            }
            Box(Modifier.fillMaxWidth().height(TabotaboTheme.dimensions.outlineWidth).background(TabotaboTheme.colors.outline))
        }
    }
}

/** The standard page heading: the page's emphasis title with its subtitle underneath. */
@Composable
internal fun PageHeading(page: SitePage) {
    ScreenHeader(
        modifier = Modifier.semantics { heading() },
        subtitle = page.subtitle,
        animateTitle = false,
        title = page.title,
    )
}

/**
 * Site header: the site name (links home; left out on the home page, whose title is the name),
 * then the section links inline on wide windows or a menu button on phones, then the settings
 * gear.
 */
@Composable
private fun SiteHeader(current: SitePage) {
    val navigate = LocalNavigate.current
    val openSettings = LocalOpenSettings.current
    val phone = LocalPhoneLayout.current
    var menuOpen by remember { mutableStateOf(false) }

    Row(Modifier.fillMaxWidth().height(HeaderHeight), verticalAlignment = Alignment.CenterVertically) {
        if (current != SitePage.Home) SiteName(onClick = { navigate(SitePage.Home) })
        Spacer(Modifier.weight(1f))
        if (phone) {
            IconAction(onClick = { menuOpen = true }, contentDescription = "Menu") { MenuIcon() }
        } else {
            SitePage.HeaderLinks.forEach { link -> HeaderLink(link, isCurrent = current.section == link) { navigate(link) } }
        }
        IconAction(onClick = openSettings, contentDescription = "Settings") { GearIcon() }
    }

    if (menuOpen) {
        MenuDialog(current = current, onNavigate = { menuOpen = false; navigate(it) }, onClose = { menuOpen = false })
    }
}

/** "Tabotabo **UI**" in the header, linking home. */
@Composable
private fun SiteName(onClick: () -> Unit) {
    Box(
        Modifier
            .heightIn(min = TouchTarget)
            .clip(RoundedCornerShape(TabotaboTheme.dimensions.cardCorner))
            .clickable(role = Role.Button, onClickLabel = "Home", onClick = onClick)
            .padding(end = TabotaboTheme.dimensions.spacing.s),
        contentAlignment = Alignment.CenterStart,
    ) {
        EmphasisText(style = TabotaboTheme.typography.titleLarge, weights = EmphasisWeights.Title, maxLines = 1) {
            UnfocusedText("Tabotabo")
            FocusedText("UI")
        }
    }
}

/** One text link in the wide header; the current section is Bold and primary. */
@Composable
private fun HeaderLink(page: SitePage, isCurrent: Boolean, onClick: () -> Unit) {
    val colors = TabotaboTheme.colors
    Box(
        Modifier
            .heightIn(min = TouchTarget)
            .clip(RoundedCornerShape(TabotaboTheme.dimensions.cardCorner))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = TabotaboTheme.dimensions.spacing.m),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = page.navLabel,
            color = if (isCurrent) colors.primary else colors.accent,
            style = TabotaboTheme.typography.labelLarge.copy(fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium),
            maxLines = 1,
        )
    }
}

/** The pages the phone menu lists, in order. */
private val MenuPages = listOf(SitePage.Home) + SitePage.HeaderLinks

/**
 * The phone menu: one full-width button per section. The section the visitor is in (a principle
 * topic counts as Principles) is the primary button and marked selected; tapping it just closes
 * the menu.
 */
@Composable
private fun MenuDialog(current: SitePage, onNavigate: (SitePage) -> Unit, onClose: () -> Unit) {
    TabotaboDialog(
        onDismissRequest = onClose,
        confirmText = "Close",
        onConfirm = onClose,
        title = { FocusedText("Menu") },
    ) {
        // The dialog leaves spacing.m under its title; this adds another, so the buttons start
        // spacing.xl (24dp) below "Menu", the same gap the dialog keeps above its Close action.
        Column(
            Modifier.fillMaxWidth().padding(top = TabotaboTheme.dimensions.spacing.m),
            verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m),
        ) {
            MenuPages.forEach { page ->
                val isCurrent = current.section == page
                val modifier = Modifier.fillMaxWidth().semantics { selected = isCurrent }
                val onClick = { if (isCurrent) onClose() else onNavigate(page) }
                if (isCurrent) {
                    PrimaryButton(page.navLabel, onClick = onClick, modifier = modifier)
                } else {
                    SecondaryButton(page.navLabel, onClick = onClick, modifier = modifier)
                }
            }
        }
    }
}

/** "Principles / Color": the parent links back, the current page is muted. */
@Composable
private fun Breadcrumb(parent: SitePage, current: SitePage) {
    val navigate = LocalNavigate.current
    val colors = TabotaboTheme.colors
    val style = TabotaboTheme.typography.labelMedium
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextLink(parent.navLabel, style = style) { navigate(parent) }
        Text(" / ", color = colors.muted, style = style)
        Text(current.navLabel, color = colors.muted, style = style.copy(fontWeight = FontWeight.Medium))
    }
}

/** Centered footer: links outside the app, then the copyright line. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SiteFooter() {
    val uriHandler = LocalUriHandler.current
    val colors = TabotaboTheme.colors
    val spacing = TabotaboTheme.dimensions.spacing
    Spacer(Modifier.height(56.dp))
    Box(Modifier.fillMaxWidth().height(TabotaboTheme.dimensions.outlineWidth).background(colors.outline))
    Column(
        Modifier.fillMaxWidth().padding(top = spacing.l, bottom = spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.s, Alignment.CenterHorizontally)) {
            ExternalLinks.forEach { (label, url) -> TextLink(label) { uriHandler.openUri(url) } }
        }
        Spacer(Modifier.height(spacing.s))
        Text("© ${currentYear()} $SITE_NAME", color = colors.muted, style = TabotaboTheme.typography.labelSmall)
    }
}

/** A Bold primary text link with a 48dp-tall touch target. */
@Composable
internal fun TextLink(
    text: String,
    style: TextStyle = TabotaboTheme.typography.labelMedium,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .heightIn(min = TouchTarget)
            .clip(RoundedCornerShape(TabotaboTheme.dimensions.cardCorner))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = TabotaboTheme.dimensions.spacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = TabotaboTheme.colors.primary, style = style.copy(fontWeight = FontWeight.Bold), maxLines = 1)
    }
}

/** Three horizontal strokes in `LocalContentColor`. */
@Composable
private fun MenuIcon() {
    val color = LocalContentColor.current
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = size.minDimension * 0.11f
        val inset = size.width * 0.15f
        listOf(0.28f, 0.5f, 0.72f).forEach { fraction ->
            val y = size.height * fraction
            drawLine(color, Offset(inset, y), Offset(size.width - inset, y), stroke, StrokeCap.Round)
        }
    }
}

/** A settings gear drawn as simple strokes in `LocalContentColor`. */
@Composable
private fun GearIcon() {
    val color = LocalContentColor.current
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = size.minDimension * 0.11f
        val center = Offset(size.width / 2, size.height / 2)
        val ring = size.minDimension * 0.24f
        drawCircle(color, ring, center, style = Stroke(stroke))
        repeat(8) { index ->
            val angle = index * PI / 4
            val direction = Offset(cos(angle).toFloat(), sin(angle).toFloat())
            drawLine(color, center + direction * (ring + stroke), center + direction * (size.minDimension / 2 - stroke / 2), stroke, StrokeCap.Round)
        }
    }
}
