package com.tabotabo.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import com.tabotabo.ui.theme.TabotaboTheme
import kotlin.time.Duration

/** Sentinel for "no text bound yet". */
private const val UNBOUND = -1

/**
 * Drives and observes one typewriter reveal. A state reveals each distinct text once: once a text
 * has fully typed in, binding the same text again (recomposition, configuration change) shows it
 * immediately, while a different text types in again.
 *
 * [revealedChars] is snapshot state; the library reads it only in the draw phase, so typing
 * redraws the text without recomposing or re-measuring it.
 */
@Stable
public class TypewriterState internal constructor(
    private val charDelay: Duration,
    private val startDelay: Duration,
    completedText: String?,
) {
    /** The last text that finished revealing; drives "run once". */
    internal var completedText: String? = completedText
        private set

    /** Called once each time a reveal completes. Kept current by [rememberTypewriterState]. */
    internal var onFinished: () -> Unit = {}

    /** Bumped by [restart] so the driving effect relaunches. */
    internal var restartToken: Int by mutableIntStateOf(0)
        private set

    private var boundText: String? = null
    private var totalChars by mutableIntStateOf(UNBOUND)
    private var revealed by mutableIntStateOf(0)

    /** Characters revealed so far. */
    public val revealedChars: Int get() = revealed

    /** True once the whole bound text is visible. */
    public val isFinished: Boolean get() = totalChars != UNBOUND && revealed >= totalChars

    /** Reveals everything immediately (e.g. on tap). */
    public fun skip() {
        if (totalChars == UNBOUND) return
        revealed = totalChars
        complete()
    }

    /** Clears the "already ran" flag and types the current text again from the start. */
    public fun restart() {
        completedText = null
        revealed = 0
        restartToken++
    }

    /**
     * Binds [text] and reveals it, one character per [charDelay] after [startDelay], timed by
     * frame clock. Reveals instantly when [animate] is false, the delay is zero, or [text] has
     * already been revealed by this state.
     */
    internal suspend fun reveal(text: String, animate: Boolean) {
        boundText = text
        totalChars = text.length
        if (!animate || !charDelay.isPositive() || completedText == text) {
            revealed = text.length
            complete()
            return
        }
        revealed = 0
        val startNanos = withFrameNanos { it } + startDelay.inWholeNanoseconds
        val perCharNanos = charDelay.inWholeNanoseconds
        while (revealed < totalChars) {
            val elapsed = withFrameNanos { it } - startNanos
            val count = (elapsed / perCharNanos).coerceIn(0L, totalChars.toLong()).toInt()
            if (count > revealed) revealed = count
        }
        complete()
    }

    private fun complete() {
        val text = boundText ?: return
        if (completedText == text) return
        completedText = text
        onFinished()
    }

    internal companion object {
        /** Saves which text already finished, so configuration changes don't replay it. */
        fun saver(charDelay: Duration, startDelay: Duration): Saver<TypewriterState, String> = Saver(
            save = { it.completedText },
            restore = { TypewriterState(charDelay, startDelay, completedText = it) },
        )
    }
}

/**
 * Remembers a [TypewriterState] that survives configuration changes.
 *
 * @param key Changing it creates a fresh state, restarting the reveal. Text changes restart it
 *   automatically.
 * @param charDelay Delay per character; defaults to the theme's `typewriterCharDelay`.
 * @param startDelay Wait before the first character, e.g. to chain a subtitle after a title.
 * @param onFinished Called once each time a reveal completes.
 */
@Composable
public fun rememberTypewriterState(
    key: Any? = null,
    charDelay: Duration = TabotaboTheme.motion.typewriterCharDelay,
    startDelay: Duration = Duration.ZERO,
    onFinished: () -> Unit = {},
): TypewriterState {
    val state = rememberSaveable(key, saver = TypewriterState.saver(charDelay, startDelay)) {
        TypewriterState(charDelay, startDelay, completedText = null)
    }
    SideEffect { state.onFinished = onFinished }
    return state
}
