package com.tabotabo.ui.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.buttonLabel

/** Whether buttons fill the available width; provided by [ActionStack]. */
internal val LocalFillActionWidth = staticCompositionLocalOf { false }

/** Size of the touch target of an [IconAction]. */
private val IconActionSize = 48.dp

/**
 * Colors of an outlined button in its enabled and disabled states.
 *
 * @property container Fill while enabled.
 * @property content Label and icon color while enabled.
 * @property border Outline color while enabled.
 * @property disabledContainer Fill while disabled.
 * @property disabledContent Label and icon color while disabled.
 * @property disabledBorder Outline color while disabled.
 */
@Immutable
public class TabotaboButtonColors(
    public val container: Color,
    public val content: Color,
    public val border: Color,
    public val disabledContainer: Color,
    public val disabledContent: Color,
    public val disabledBorder: Color,
) {
    /** Returns a copy with any of the given values replaced. */
    public fun copy(
        container: Color = this.container,
        content: Color = this.content,
        border: Color = this.border,
        disabledContainer: Color = this.disabledContainer,
        disabledContent: Color = this.disabledContent,
        disabledBorder: Color = this.disabledBorder,
    ): TabotaboButtonColors =
        TabotaboButtonColors(container, content, border, disabledContainer, disabledContent, disabledBorder)

    /** Fill for the given [enabled] state. */
    internal fun container(enabled: Boolean): Color = if (enabled) container else disabledContainer

    /** Label and icon color for the given [enabled] state. */
    internal fun content(enabled: Boolean): Color = if (enabled) content else disabledContent

    /** Outline color for the given [enabled] state. */
    internal fun border(enabled: Boolean): Color = if (enabled) border else disabledBorder

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TabotaboButtonColors) return false
        return container == other.container && content == other.content && border == other.border &&
            disabledContainer == other.disabledContainer && disabledContent == other.disabledContent &&
            disabledBorder == other.disabledBorder
    }

    override fun hashCode(): Int {
        var result = container.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + border.hashCode()
        result = 31 * result + disabledContainer.hashCode()
        result = 31 * result + disabledContent.hashCode()
        result = 31 * result + disabledBorder.hashCode()
        return result
    }
}

/** Defaults shared by [PrimaryButton] and [SecondaryButton]. */
public object TabotaboButtonDefaults {
    /** Primary: `surface` fill, `primary` border and content; disabled uses `disabled`. */
    @Composable
    public fun primaryColors(): TabotaboButtonColors {
        val colors = TabotaboTheme.colors
        return TabotaboButtonColors(
            container = colors.surface,
            content = colors.primary,
            border = colors.primary,
            disabledContainer = colors.surface,
            disabledContent = colors.disabled,
            disabledBorder = colors.disabled,
        )
    }

    /** Secondary: `surface` fill, `outlineStrong` border, `accent` content; disabled uses `disabled`. */
    @Composable
    public fun secondaryColors(): TabotaboButtonColors {
        val colors = TabotaboTheme.colors
        return TabotaboButtonColors(
            container = colors.surface,
            content = colors.accent,
            border = colors.outlineStrong,
            disabledContainer = colors.surface,
            disabledContent = colors.disabled,
            disabledBorder = colors.disabled,
        )
    }

    /** Button corner shape. */
    public val Shape: Shape = RoundedCornerShape(12.dp)

    /** Outline width. */
    public val BorderWidth: Dp = 1.5.dp

    /** Minimum button height. */
    public val MinHeight: Dp = 52.dp

    /** Padding around the button content. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 14.dp)

    /** Size of leading / trailing icons. */
    public val IconSize: Dp = 20.dp

    /** Gap between an icon and the label. */
    public val IconSpacing: Dp = 8.dp
}

/**
 * Outlined primary action. Content color flows through `LocalContentColor`, so icons are tinted
 * automatically; the disabled state uses the theme's `disabled` color and ignores clicks.
 */
@Composable
public fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TabotaboButtonColors = TabotaboButtonDefaults.primaryColors(),
    shape: Shape = TabotaboButtonDefaults.Shape,
    borderWidth: Dp = TabotaboButtonDefaults.BorderWidth,
    contentPadding: PaddingValues = TabotaboButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedActionButton(onClick, modifier, enabled, colors, shape, borderWidth, contentPadding, interactionSource, content)
}

/** Text convenience overload: a Bold single-line label that marquees instead of wrapping. */
@Composable
public fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: TabotaboButtonColors = TabotaboButtonDefaults.primaryColors(),
) {
    PrimaryButton(onClick = onClick, modifier = modifier, enabled = enabled, colors = colors) {
        ButtonLabel(text, leadingIcon, trailingIcon)
    }
}

/** Outlined secondary action; see [PrimaryButton] for behaviour. */
@Composable
public fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TabotaboButtonColors = TabotaboButtonDefaults.secondaryColors(),
    shape: Shape = TabotaboButtonDefaults.Shape,
    borderWidth: Dp = TabotaboButtonDefaults.BorderWidth,
    contentPadding: PaddingValues = TabotaboButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedActionButton(onClick, modifier, enabled, colors, shape, borderWidth, contentPadding, interactionSource, content)
}

/** Text convenience overload: a Bold single-line label that marquees instead of wrapping. */
@Composable
public fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: TabotaboButtonColors = TabotaboButtonDefaults.secondaryColors(),
) {
    SecondaryButton(onClick = onClick, modifier = modifier, enabled = enabled, colors = colors) {
        ButtonLabel(text, leadingIcon, trailingIcon)
    }
}

/**
 * The shared body of every button: an outlined, rounded surface whose colors animate between the
 * enabled and disabled states, with centered [content] in the button label style.
 */
@Composable
private fun OutlinedActionButton(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    colors: TabotaboButtonColors,
    shape: Shape,
    borderWidth: Dp,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource?,
    content: @Composable RowScope.() -> Unit,
) {
    val spec = tween<Color>(TabotaboTheme.motion.highlightMillis)
    val container by animateColorAsState(colors.container(enabled), spec, label = "buttonContainer")
    val contentColor by animateColorAsState(colors.content(enabled), spec, label = "buttonContent")
    val border by animateColorAsState(colors.border(enabled), spec, label = "buttonBorder")
    val width = if (LocalFillActionWidth.current) Modifier.fillMaxWidth() else Modifier

    Row(
        modifier = width
            .then(modifier)
            .defaultMinSize(minHeight = TabotaboButtonDefaults.MinHeight)
            .clip(shape)
            .background(container)
            .border(borderWidth, border, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = contentColor),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(contentPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides TabotaboTheme.typography.buttonLabel,
        ) {
            content()
        }
    }
}

/** A button's icons and single-line marquee label. */
@Composable
private fun RowScope.ButtonLabel(
    text: String,
    leadingIcon: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?,
) {
    if (leadingIcon != null) {
        Box(Modifier.size(TabotaboButtonDefaults.IconSize), contentAlignment = Alignment.Center) { leadingIcon() }
        Spacer(Modifier.width(TabotaboButtonDefaults.IconSpacing))
    }
    Text(
        text = text,
        maxLines = 1,
        textAlign = TextAlign.Center,
        modifier = Modifier.weight(1f, fill = false).basicMarquee(iterations = Int.MAX_VALUE),
    )
    if (trailingIcon != null) {
        Spacer(Modifier.width(TabotaboButtonDefaults.IconSpacing))
        Box(Modifier.size(TabotaboButtonDefaults.IconSize), contentAlignment = Alignment.Center) { trailingIcon() }
    }
}

/**
 * Stacks buttons vertically, each full width within the stack, with the theme's medium spacing
 * between them. [maxWidth] caps the width on wide / web layouts (skill: ~480dp).
 *
 * @param alignment Where a width-capped stack sits in the available width: centred by default,
 *   or e.g. [Alignment.Start] to line up with left-aligned content such as an article.
 */
@Composable
public fun ActionStack(
    modifier: Modifier = Modifier,
    maxWidth: Dp = Dp.Unspecified,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Column(
            modifier = Modifier
                .then(if (maxWidth != Dp.Unspecified) Modifier.widthIn(max = maxWidth) else Modifier)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CompositionLocalProvider(LocalFillActionWidth provides true) { content() }
        }
    }
}

/**
 * Bare icon action (back arrow, info, settings gear): no card, [tint] via `LocalContentColor`,
 * and a 48dp circular touch target.
 */
@Composable
public fun IconAction(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = TabotaboTheme.colors.primary,
    icon: @Composable () -> Unit,
) {
    val colors = TabotaboTheme.colors
    Box(
        modifier = modifier
            .size(IconActionSize)
            .clip(CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides if (enabled) tint else colors.disabled) { icon() }
    }
}
