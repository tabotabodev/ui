package com.tabotabo.ui.text

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.EmphasisWeights
import com.tabotabo.ui.typography.body
import com.tabotabo.ui.typography.factTitle
import com.tabotabo.ui.typography.heroTitle
import com.tabotabo.ui.typography.screenTitle

/** Large left-aligned screen title in [EmphasisWeights.Title]; types in when [animate]. */
@Composable
public fun ScreenTitle(
    modifier: Modifier = Modifier,
    animate: Boolean = true,
    content: EmphasisTextScope.() -> Unit,
) {
    EmphasisLineRole(modifier, animate, TabotaboTheme.typography.screenTitle, EmphasisWeights.Title, content)
}

/** Hero (42sp) emphasis line in [EmphasisWeights.Headline] for landing / splash screens; types in when [animate]. */
@Composable
public fun HeroTitle(
    modifier: Modifier = Modifier,
    animate: Boolean = true,
    content: EmphasisTextScope.() -> Unit,
) {
    EmphasisLineRole(modifier, animate, TabotaboTheme.typography.heroTitle, EmphasisWeights.Headline, content)
}

/** Shared body of the emphasis roles: a [TypewriterText] or a static [EmphasisText]. */
@Composable
private fun EmphasisLineRole(
    modifier: Modifier,
    animate: Boolean,
    style: TextStyle,
    weights: EmphasisWeights,
    content: EmphasisTextScope.() -> Unit,
) {
    if (animate) {
        TypewriterText(modifier = modifier, style = style, weights = weights, content = content)
    } else {
        EmphasisText(modifier = modifier, style = style, weights = weights, content = content)
    }
}

/**
 * Muted line that sits directly under a headline.
 *
 * @param minLines Reserve lines so a subtitle that changes length never shifts the layout.
 */
@Composable
public fun Subtitle(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
    minLines: Int = 1,
) {
    Text(
        text = text,
        modifier = modifier,
        color = TabotaboTheme.colors.muted,
        style = TabotaboTheme.typography.body,
        maxLines = maxLines,
        minLines = minLines,
        overflow = TextOverflow.Ellipsis,
    )
}

/** Title/value fact, e.g. "App version" over "1.2.0". */
@Composable
public fun Fact(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = title, color = TabotaboTheme.colors.accent, style = TabotaboTheme.typography.factTitle)
        Text(text = value, color = TabotaboTheme.colors.muted, style = TabotaboTheme.typography.body)
    }
}
