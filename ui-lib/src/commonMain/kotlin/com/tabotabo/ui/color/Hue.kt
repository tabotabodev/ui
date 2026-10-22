package com.tabotabo.ui.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlin.jvm.JvmInline
import kotlin.random.Random

/** Number of degrees in a full turn of the color wheel. */
private const val FULL_TURN = 360f

/**
 * A hue angle in degrees, always normalised to `[0, 360)`. Create one with `Hue(degrees)`, which
 * wraps any angle (negative or beyond a full turn).
 */
@JvmInline
public value class Hue private constructor(public val degrees: Float) {

    /** Returns this hue rotated by [degrees] and wrapped into `[0, 360)`. */
    public fun rotate(degrees: Float): Hue = Companion(this.degrees + degrees)

    public companion object {
        /** Creates a [Hue] from any angle in [degrees], wrapping it into `[0, 360)`. */
        public operator fun invoke(degrees: Float): Hue {
            val wrapped = degrees % FULL_TURN
            val normalised = if (wrapped < 0f) wrapped + FULL_TURN else wrapped
            // `-0.0001 % 360 + 360` can round to exactly 360 in Float; fold it back to 0.
            return Hue(if (normalised >= FULL_TURN) 0f else normalised)
        }

        /** Returns a uniformly random hue. [random] is injectable for deterministic tests. */
        public fun random(random: Random = Random.Default): Hue = invoke(random.nextFloat() * FULL_TURN)
    }
}

/** Shortest angular distance between two hues, in `[0, 180]`. */
internal fun Hue.distanceTo(other: Hue): Float {
    val difference = kotlin.math.abs(degrees - other.degrees)
    return minOf(difference, FULL_TURN - difference)
}

/**
 * Holds the session hue. The UI can re-roll it with [shuffle] ("Shuffle colors") or set a specific
 * one through [hue] (e.g. from a hue slider). Reading [hue] in composition subscribes to changes.
 */
@Stable
public class HueState(initial: Hue) {

    /**
     * The current hue. Setting it recolors everything themed from this state; any angle is
     * accepted, since [Hue] is already normalised to `[0, 360)`.
     */
    public var hue: Hue by mutableStateOf(initial)

    /**
     * Replaces [hue] with a random hue at least [minDistance] degrees away from the current one,
     * so a shuffle always looks like a change. [minDistance] is clamped to `[0, 180]`.
     */
    public fun shuffle(random: Random = Random.Default, minDistance: Float = 45f) {
        val distance = minDistance.coerceIn(0f, FULL_TURN / 2)
        val offset = distance + random.nextFloat() * (FULL_TURN - 2 * distance)
        hue = hue.rotate(offset)
    }

    public companion object {
        /** Saves the hue as a Float so it survives configuration changes. */
        public val Saver: Saver<HueState, Float> = Saver(
            save = { it.hue.degrees },
            restore = { HueState(Hue(it)) },
        )
    }
}

/**
 * Remembers a [HueState] for the session. The hue survives recomposition and configuration
 * changes; a new one is generated on every cold launch / page load.
 *
 * @param initial Produces the first hue; called only when no saved state exists.
 */
@Composable
public fun rememberHueState(initial: () -> Hue = { Hue.random() }): HueState =
    rememberSaveable(saver = HueState.Saver) { HueState(initial()) }
