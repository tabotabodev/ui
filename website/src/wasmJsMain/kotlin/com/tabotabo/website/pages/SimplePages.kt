package com.tabotabo.website.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.TabotaboButtonDefaults
import com.tabotabo.ui.text.EmphasisText
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.EmphasisWeights
import com.tabotabo.website.LocalNavigate
import com.tabotabo.website.Paragraph
import com.tabotabo.website.SitePage
import com.tabotabo.website.SitePageScaffold

/**
 * Shown for any address that isn't a page of the site. It uses Material's error color (which the
 * theme leaves at Material's own values) where other pages use primary: the title's key phrase
 * and the outline and label of the "Go home" button.
 */
@Composable
internal fun NotFoundPage() {
    val navigate = LocalNavigate.current
    val page = SitePage.NotFound
    val error = MaterialTheme.colorScheme.error
    SitePageScaffold(
        page = page,
        heading = {
            Column(Modifier.fillMaxWidth()) {
                EmphasisText(
                    modifier = Modifier.semantics { heading() },
                    style = TabotaboTheme.typography.headlineLarge,
                    weights = EmphasisWeights.Title,
                    focusedColor = error,
                    content = page.title,
                )
                Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xs))
                Subtitle(page.subtitle, maxLines = Int.MAX_VALUE)
            }
        },
    ) {
        Paragraph { +"Head back to the start, or use the menu to find what you were looking for." }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xl))
        PrimaryButton(
            "Go home",
            onClick = { navigate(SitePage.Home) },
            modifier = Modifier.widthIn(max = 320.dp),
            colors = TabotaboButtonDefaults.primaryColors().copy(content = error, border = error),
        )
    }
}
