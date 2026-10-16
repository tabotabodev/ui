package com.tabotabo.ui.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.tabotabo.ui.theme.TabotaboTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/** Scale a [PopInContent] value starts from. */
private const val POP_IN_INITIAL_SCALE = 0.8f

/** Peak scale of a [celebrate] bounce. */
private const val CELEBRATE_PEAK_SCALE = 1.15f

/** Lowest alpha of a [pulse]. */
private const val PULSE_MIN_ALPHA = 0.45f

/** Duration of one half of a [pulse] cycle. */
private const val PULSE_HALF_CYCLE_MILLIS = 700

/**
 * Animation tokens. All animations use Compose's own APIs; no extra libraries.
 *
 * @property enabled False turns every animation off (e.g. when the OS asks to reduce motion).
 * @property typewriterCharDelay Delay per character of a typewriter reveal.
 * @property highlightMillis Duration of outline / glow / color changes on highlight.
 * @property crossfadeMillis Duration of fades between states.
 * @property popIn Scale spec of a value "popping" in.
 * @property celebrate Scale spec of a celebratory bounce.
 */
@Immutable
public data class TabotaboMotion(
    val enabled: Boolean = true,
    val typewriterCharDelay: Duration = 35.milliseconds,
    val highlightMillis: Int = 250,
    val crossfadeMillis: Int = 200,
    val popIn: FiniteAnimationSpec<Float> = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
    val celebrate: FiniteAnimationSpec<Float> = spring(dampingRatio = Spring.DampingRatioHighBouncy),
) {
    public companion object {
        /** The standard motion tokens. */
        public val Default: TabotaboMotion = TabotaboMotion()

        /** Every duration collapses to instant; typewriter text reveals immediately. */
        public val Reduced: TabotaboMotion = TabotaboMotion(
            enabled = false,
            typewriterCharDelay = Duration.ZERO,
            highlightMillis = 0,
            crossfadeMillis = 0,
            popIn = snap(),
            celebrate = snap(),
        )
    }
}

/** Fade + scale-in when [targetState] changes (a value or symbol "pops" in). */
@Composable
public fun <S> PopInContent(
    targetState: S,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable AnimatedContentScope.(S) -> Unit,
) {
    val motion = TabotaboTheme.motion
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            if (motion.enabled) {
                (fadeIn(tween(motion.crossfadeMillis)) + scaleIn(motion.popIn, POP_IN_INITIAL_SCALE))
                    .togetherWith(fadeOut(tween(motion.crossfadeMillis)))
            } else {
                EnterTransition.None.togetherWith(ExitTransition.None)
            }
        },
        contentAlignment = contentAlignment,
        label = "PopInContent",
        content = content,
    )
}

/** Gentle alpha pulse for "thinking / working" labels while [active]. */
public fun Modifier.pulse(active: Boolean): Modifier = composed {
    if (!active || !TabotaboTheme.motion.enabled) return@composed this
    val alpha = rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = PULSE_MIN_ALPHA,
        animationSpec = infiniteRepeatable(tween(PULSE_HALF_CYCLE_MILLIS), RepeatMode.Reverse),
        label = "pulseAlpha",
    )
    graphicsLayer { this.alpha = alpha.value }
}

/**
 * One bouncy scale-up each time [trigger] changes (not on first composition). Use only for
 * celebratory moments.
 */
public fun Modifier.celebrate(trigger: Any?): Modifier = composed {
    val motion = TabotaboTheme.motion
    val scale = remember { Animatable(1f) }
    val initialTrigger = remember { trigger }
    LaunchedEffect(trigger) {
        if (trigger == initialTrigger || !motion.enabled) return@LaunchedEffect
        scale.snapTo(1f)
        scale.animateTo(CELEBRATE_PEAK_SCALE, motion.celebrate)
        scale.animateTo(1f, motion.celebrate)
    }
    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}
