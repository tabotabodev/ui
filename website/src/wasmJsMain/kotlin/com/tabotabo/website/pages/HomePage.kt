package com.tabotabo.website.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.surface.Tag
import com.tabotabo.ui.text.HeroTitle
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.website.CodeHint
import com.tabotabo.website.INSTALL_LINE
import com.tabotabo.website.LocalNavigate
import com.tabotabo.website.LocalPhoneLayout
import com.tabotabo.website.Paragraph
import com.tabotabo.website.SitePage
import com.tabotabo.website.SitePageScaffold

/** Platforms the library runs on, shown as tags. */
private val Platforms = listOf("Android", "iOS", "Desktop", "Web")

/**
 * Home: only what Tabotabo UI is. A typed-in hero title, one paragraph, the platforms, the install
 * line, and two ways onward. Fits in one to two phone screens.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun HomePage() {
    val spacing = TabotaboTheme.dimensions.spacing
    val page = SitePage.Home
    SitePageScaffold(
        page = page,
        heading = {
            Column(Modifier.fillMaxWidth()) {
                HeroTitle(modifier = Modifier.semantics { heading() }, content = page.title)
                Spacer(Modifier.height(spacing.xs))
                Subtitle(page.subtitle, maxLines = Int.MAX_VALUE)
            }
        },
    ) {
        Paragraph {
            +"A design system for Compose Multiplatform. Type, one color and calm spacing carry the design, so apps on "
            +"Android, iOS, desktop and the web feel like one family. "
            bold("Everything on this site is drawn by the library itself")
            +", running as a Kotlin/Wasm app."
        }
        Spacer(Modifier.height(spacing.l))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.s),
            verticalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            Platforms.forEach { Tag(it) }
        }
        Spacer(Modifier.height(spacing.xl))
        CodeHint(INSTALL_LINE)
        Spacer(Modifier.height(spacing.xl))
        HomeActions()
    }
}

/** "Get started" and "Explore the principles": a row when wide, stacked full width on a phone. */
@Composable
private fun HomeActions() {
    val navigate = LocalNavigate.current
    val spacing = TabotaboTheme.dimensions.spacing
    val start: @Composable (Modifier) -> Unit = { modifier ->
        PrimaryButton("Get started", onClick = { navigate(SitePage.Start) }, modifier = modifier)
    }
    val principles: @Composable (Modifier) -> Unit = { modifier ->
        SecondaryButton("Explore the principles", onClick = { navigate(SitePage.Principles) }, modifier = modifier)
    }
    if (LocalPhoneLayout.current) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.m)) {
            start(Modifier.fillMaxWidth())
            principles(Modifier.fillMaxWidth())
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.m)) {
            start(Modifier)
            principles(Modifier)
        }
    }
}
