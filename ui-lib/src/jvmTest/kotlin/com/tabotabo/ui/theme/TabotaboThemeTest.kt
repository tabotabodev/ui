package com.tabotabo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.tabotabo.ui.TestTheme
import com.tabotabo.ui.button.TabotaboButtonColors
import com.tabotabo.ui.button.TabotaboButtonDefaults
import com.tabotabo.ui.color.Hue
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.color.TabotaboColors
import com.tabotabo.ui.color.tabotaboColors
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TabotaboThemeTest {

    @Test
    fun themeProvidesThePaletteForItsHueAndMode() = runComposeUiTest {
        lateinit var colors: TabotaboColors
        var materialPrimary = Color.Unspecified
        setContent {
            TestTheme(darkTheme = false) {
                colors = TabotaboTheme.colors
                materialPrimary = MaterialTheme.colorScheme.primary
            }
        }
        runOnIdle {
            assertEquals(tabotaboColors(Hue(200f), isDark = false), colors)
            assertEquals(colors.primary, materialPrimary)
        }
    }

    @Test
    fun darkThemeComesFromTheConsumer() = runComposeUiTest {
        var dark by mutableStateOf(true)
        lateinit var colors: TabotaboColors
        setContent {
            TestTheme(darkTheme = dark) { colors = TabotaboTheme.colors }
        }
        runOnIdle { assertTrue(colors.isDark) }
        dark = false
        runOnIdle {
            assertFalse(colors.isDark)
            assertEquals(Hue(200f), colors.hue)
        }
    }

    @Test
    fun systemBarsFollowShufflesAndModeChanges() = runComposeUiTest {
        val reported = mutableListOf<TabotaboColors>()
        val hueState = HueState(Hue(200f))
        var dark by mutableStateOf(true)
        setContent {
            TestTheme(darkTheme = dark, hueState = hueState, systemBars = { reported += it }) {}
        }
        runOnIdle { assertEquals(Hue(200f), reported.last().hue) }

        runOnIdle { hueState.shuffle(Random(4)) }
        runOnIdle { assertNotEquals(Hue(200f), reported.last().hue) }

        dark = false
        runOnIdle { assertFalse(reported.last().isDark) }
    }

    @Test
    fun buttonDefaultsFollowTheSkill() = runComposeUiTest {
        lateinit var colors: TabotaboColors
        lateinit var primary: TabotaboButtonColors
        lateinit var secondary: TabotaboButtonColors
        setContent {
            TestTheme {
                colors = TabotaboTheme.colors
                primary = TabotaboButtonDefaults.primaryColors()
                secondary = TabotaboButtonDefaults.secondaryColors()
            }
        }
        runOnIdle {
            assertEquals(colors.surface, primary.container)
            assertEquals(colors.primary, primary.border)
            assertEquals(colors.primary, primary.content)
            assertEquals(colors.surface, secondary.container)
            assertEquals(colors.outlineStrong, secondary.border)
            assertEquals(colors.accent, secondary.content)
            for (button in listOf(primary, secondary)) {
                assertEquals(colors.disabled, button.disabledBorder)
                assertEquals(colors.disabled, button.disabledContent)
            }
        }
    }
}
