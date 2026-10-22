package com.tabotabo.website.pages.principles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.color.ContrastTargets
import com.tabotabo.ui.color.Hue
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.color.TabotaboColors
import com.tabotabo.ui.color.contrastRatio
import com.tabotabo.ui.color.tabotaboColors
import com.tabotabo.ui.settings.ShuffleColorsButton
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.surface.Tag
import com.tabotabo.ui.text.Fact
import com.tabotabo.ui.text.ScreenTitle
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.website.CodeHint
import com.tabotabo.website.LIBRARY_VERSION
import com.tabotabo.website.LocalPhoneLayout
import com.tabotabo.website.Paragraph
import com.tabotabo.website.SitePage
import com.tabotabo.website.SitePageScaffold
import com.tabotabo.website.SpecTable
import com.tabotabo.website.SubSection
import com.tabotabo.website.ThemePreview
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * One color role as the decision doc defines it.
 *
 * @property name Role name in the library.
 * @property use What the role is for.
 * @property light Documented saturation / lightness in light mode, before any contrast adjustment.
 * @property dark The same for dark mode.
 * @property target Contrast it is adjusted to against background and surface, or null if none.
 * @property pick Reads the role from a generated palette.
 */
private class RoleSpec(
    val name: String,
    val use: String,
    val light: Pair<Float, Float>,
    val dark: Pair<Float, Float>,
    val target: Double?,
    val pick: (TabotaboColors) -> Color,
)

/** The tone table from docs/design-decisions.md §1.3, used only to show the formulas. */
private val Roles = listOf(
    RoleSpec("background", "Screen background", 0.45f to 0.97f, 0.60f to 0.04f, null) { it.background },
    RoleSpec("surface", "Card, button and dialog fill", 0.30f to 0.995f, 0.35f to 0.10f, null) { it.surface },
    RoleSpec("primary", "Key phrases, highlights, primary actions", 0.85f to 0.40f, 0.95f to 0.58f, ContrastTargets.Text) { it.primary },
    RoleSpec("accent", "Body text", 0.35f to 0.14f, 0.45f to 0.88f, ContrastTargets.Text) { it.accent },
    RoleSpec("muted", "Subtitles and descriptions", 0.18f to 0.40f, 0.25f to 0.62f, ContrastTargets.Text) { it.muted },
    RoleSpec("outline", "Card, dialog and tag borders", 0.20f to 0.82f, 0.20f to 0.24f, null) { it.outline },
    RoleSpec("outlineStrong", "Borders of buttons and pills", 0.20f to 0.82f, 0.20f to 0.24f, ContrastTargets.Graphics) { it.outlineStrong },
)

/** Labels for the distinct-color samples: tags that need telling apart. */
private val DistinctLabels = listOf("Design", "Travel", "Work", "Home", "Music", "Books")

/**
 * Color: the hue slider (recolors the whole site), the pipeline, the live role table, the
 * contrast adjustment, derived roles, a preview, distinct colors and the Material 3 mapping.
 * Every number is computed from the palette the library generates for the current hue.
 */
@Composable
internal fun ColorPage(hueState: HueState) {
    val hue = hueState.hue
    val light = remember(hue) { tabotaboColors(hue, isDark = false) }
    val dark = remember(hue) { tabotaboColors(hue, isDark = true) }
    SitePageScaffold(page = SitePage.Color) {
        Paragraph {
            +"Everything starts from one number: a hue, picked at random on every launch. Each color role is that hue "
            +"at a fixed saturation and lightness, and text roles are then adjusted until they are readable. Drag the "
            +"hue to recolor this whole site."
        }
        HueControl(hueState)
        PipelineSection()
        RolesSection(light, dark)
        AdjustmentSection(light, dark)
        DerivedSection()
        PreviewSection(hueState)
        DistinctSection(hueState)
        MaterialSection()
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xl))
    }
}

// ---------------------------------------------------------------- Hue

/** The site's hue as a slider over the color wheel, plus Shuffle. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HueControl(hueState: HueState) {
    val spacing = TabotaboTheme.dimensions.spacing
    val degrees = hueState.hue.degrees
    GlowCard(Modifier.fillMaxWidth().padding(top = spacing.l)) {
        Text("h = ${degrees.roundToInt()}°", color = TabotaboTheme.colors.accent, style = TabotaboTheme.typography.titleMedium)
        Slider(
            value = degrees,
            onValueChange = { hueState.hue = Hue(it) },
            valueRange = 0f..359f,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Hue" },
            colors = SliderDefaults.colors(thumbColor = TabotaboTheme.colors.primary),
            track = { HueTrack() },
        )
        Subtitle("Drag to recolor the site. A reload picks a new hue.", maxLines = Int.MAX_VALUE)
        Spacer(Modifier.height(spacing.m))
        ShuffleColorsButton(hueState = hueState, modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth())
    }
}

/** The slider track: the whole color wheel, so the thumb sits on the hue it picks. */
@Composable
private fun HueTrack() {
    val wheel = remember { (0..12).map { Color.hsl(it * 30f % 360f, 0.85f, 0.5f) } }
    Box(Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(Brush.horizontalGradient(wheel)))
}

// ---------------------------------------------------------------- Pipeline

@Composable
private fun PipelineSection() {
    val steps = listOf(
        "Pick a hue" to "Random, 0–360°, on every launch. Shuffle moves at least 45° away.",
        "Look up the tone" to "Each role has a fixed saturation and lightness for light and for dark.",
        "Build the color" to "hsl(h, S, L): the same hue for every role.",
        "Adjust for contrast" to "Text roles get lighter or darker until they are readable.",
        "Derive the rest" to "onPrimary, disabled and scrim come from the roles above.",
        "Map onto Material 3" to "So stock Material components follow the scheme too.",
    )
    SubSection(title = { UnfocusedText("From hue to"); FocusedText("palette") }) {
        Column(Modifier.padding(top = TabotaboTheme.dimensions.spacing.l), verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m)) {
            steps.forEachIndexed { index, (title, detail) -> PipelineStep(index + 1, title, detail) }
        }
    }
}

@Composable
private fun PipelineStep(number: Int, title: String, detail: String) {
    val colors = TabotaboTheme.colors
    Row {
        Text("$number", Modifier.width(32.dp), color = colors.primary, style = TabotaboTheme.typography.titleMedium)
        Column(Modifier.weight(1f)) {
            Text(title, color = colors.accent, style = TabotaboTheme.typography.labelLarge)
            Text(detail, color = colors.muted, style = TabotaboTheme.typography.bodyMedium)
        }
    }
}

// ---------------------------------------------------------------- Roles

@Composable
private fun RolesSection(light: TabotaboColors, dark: TabotaboColors) {
    SubSection(title = { UnfocusedText("Role"); FocusedText("formulas") }) {
        Paragraph {
            +"Each role is hsl(h, S, L). The swatches and numbers below are the library's real output for the current "
            +"hue. Contrast is measured against the background and against cards."
        }
        val phone = LocalPhoneLayout.current
        Column(Modifier.padding(top = TabotaboTheme.dimensions.spacing.l), verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s)) {
            Roles.forEach { role -> if (phone) RoleCard(role, light, dark) else RoleRow(role, light, dark) }
        }
    }
}

/** Wide layout: role name and use, then the light and dark results side by side. */
@Composable
private fun RoleRow(role: RoleSpec, light: TabotaboColors, dark: TabotaboColors) {
    val spacing = TabotaboTheme.dimensions.spacing
    Column {
        Row(Modifier.fillMaxWidth().padding(vertical = spacing.m), horizontalArrangement = Arrangement.spacedBy(spacing.l)) {
            RoleName(role, Modifier.weight(1f))
            ModeResult(role, light, isDark = false, Modifier.weight(1.3f))
            ModeResult(role, dark, isDark = true, Modifier.weight(1.3f))
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(TabotaboTheme.colors.outline))
    }
}

/** Phone layout: a card per role with the light result above the dark one. */
@Composable
private fun RoleCard(role: RoleSpec, light: TabotaboColors, dark: TabotaboColors) {
    val spacing = TabotaboTheme.dimensions.spacing
    GlowCard(Modifier.fillMaxWidth()) {
        RoleName(role)
        Spacer(Modifier.height(spacing.m))
        ModeResult(role, light, isDark = false)
        Spacer(Modifier.height(spacing.m))
        ModeResult(role, dark, isDark = true)
    }
}

@Composable
private fun RoleName(role: RoleSpec, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(role.name, color = TabotaboTheme.colors.accent, style = TabotaboTheme.typography.labelLarge)
        Text(role.use, color = TabotaboTheme.colors.muted, style = TabotaboTheme.typography.bodySmall)
    }
}

/**
 * One mode's result for [role]: swatch and hex, the formula, how far the lightness moved, and the
 * contrast against background and surface (primary, marked "passes", when it meets the role's target).
 */
@Composable
private fun ModeResult(role: RoleSpec, palette: TabotaboColors, isDark: Boolean, modifier: Modifier = Modifier) {
    val colors = TabotaboTheme.colors
    val typography = TabotaboTheme.typography
    val color = role.pick(palette)
    val (saturation, baseLightness) = if (isDark) role.dark else role.light
    val finalLightness = color.hslLightness()
    val moved = ((finalLightness - baseLightness) / 0.01f).roundToInt()
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(28.dp).clip(RoundedCornerShape(6.dp)).background(color).border(1.dp, colors.outline, RoundedCornerShape(6.dp)))
            Spacer(Modifier.width(TabotaboTheme.dimensions.spacing.s))
            Column {
                Text("${if (isDark) "Dark" else "Light"} · ${color.hex()}", color = colors.accent, style = typography.labelMedium)
                Text("hsl(h, ${saturation.fmt()}, ${baseLightness.fmt()})", color = colors.muted, style = typography.bodySmall)
            }
        }
        if (moved != 0) {
            Text(
                "L ${baseLightness.fmt()} to ${finalLightness.fmt()} (${abs(moved)} step${if (abs(moved) == 1) "" else "s"})",
                color = colors.muted,
                style = typography.bodySmall,
            )
        }
        if (role.name != "background") {
            val onBackground = contrastRatio(color, palette.background)
            val onSurface = contrastRatio(color, palette.surface)
            val target = role.target
            val passes = target != null && onBackground >= target && onSurface >= target
            val text = buildString {
                append("${onBackground.fmt()} on background")
                if (role.name != "surface") append(" · ${onSurface.fmt()} on cards")
                when {
                    passes -> append(" · passes")
                    target == null && role.name == "outline" -> append(" · soft on purpose")
                }
            }
            Text(text, color = if (passes) colors.primary else colors.muted, style = typography.bodySmall)
        }
    }
}

// ---------------------------------------------------------------- Adjustment

@Composable
private fun AdjustmentSection(light: TabotaboColors, dark: TabotaboColors) {
    val moved = buildList {
        for ((palette, mode) in listOf(light to "light", dark to "dark")) {
            Roles.filter { it.target != null }.forEach { role ->
                val base = if (palette.isDark) role.dark.second else role.light.second
                val final = role.pick(palette).hslLightness()
                if (abs(final - base) >= 0.005f) add("$mode ${role.name} ${base.fmt()} to ${final.fmt()}")
            }
        }
    }
    SubSection(title = { UnfocusedText("Readable at"); FocusedText("any hue") }) {
        Paragraph {
            +"Text roles move their lightness away from the background, 0.01 at a time, until they reach "
            bold("4.5:1")
            +" against both the background and cards. Strong outlines do the same to "
            bold("3:1")
            +". Hue and saturation never change."
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        CodeHint(
            """
            ratio(a, b) = (lighter + 0.05) / (darker + 0.05)   // relative luminance
            direction   = background is dark ? lighter : darker
            while ratio(color, background) < target or ratio(color, surface) < target:
                L = L ± 0.01;  color = hsl(h, S, L)
            """,
        )
        Paragraph {
            if (moved.isEmpty()) {
                +"At this hue, no role needed adjusting."
            } else {
                +"At this hue: "
                bold(moved.joinToString(", "))
                +"."
            }
        }
    }
}

// ---------------------------------------------------------------- Derived

@Composable
private fun DerivedSection() {
    SubSection(title = { UnfocusedText("Derived"); FocusedText("roles") }) {
        SpecTable(
            headers = listOf("Role", "Formula", "Used for"),
            rows = listOf(
                listOf("onPrimary", "= background", "Content on a primary fill (rare: buttons are outlined)"),
                listOf("disabled", "= muted at 38% opacity", "Labels and outlines of disabled controls"),
                listOf("scrim", "= background at 60% opacity", "Behind dialogs and sheets"),
                listOf("error", "Material's own", "Error messages (not derived from the hue)"),
            ),
            weights = listOf(1f, 1.4f, 2f),
        )
    }
}

// ---------------------------------------------------------------- Preview

@Composable
private fun PreviewSection(hueState: HueState) {
    SubSection(title = { UnfocusedText("All together"); FocusedText("live") }) {
        Paragraph { +"A settings screen built only from these roles. Switch between light and dark." }
        ThemePreview(hueState) { SettingsMock() }
    }
}

/** A small settings screen made of real components. */
@Composable
private fun SettingsMock() {
    val colors = TabotaboTheme.colors
    val spacing = TabotaboTheme.dimensions.spacing
    var appearance by remember { mutableStateOf("System") }
    ScreenTitle(animate = false) { FocusedText("Settings") }
    Subtitle("Make it yours. Colors change every launch.", maxLines = Int.MAX_VALUE)
    GlowCard(Modifier.fillMaxWidth()) {
        Text("Appearance", color = colors.accent, style = TabotaboTheme.typography.labelLarge)
        Subtitle("Pick light or dark, or follow your device.", maxLines = Int.MAX_VALUE)
        Spacer(Modifier.height(spacing.m))
        SelectablePillRow(listOf("Light", "Dark", "System"), appearance, { appearance = it }, label = { it })
    }
    GlowCard(Modifier.fillMaxWidth(), highlighted = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Shuffle colors", Modifier.weight(1f), color = colors.accent, style = TabotaboTheme.typography.labelLarge)
            Tag("New", color = colors.primary)
        }
        Subtitle("Pick a fresh hue for this session.", maxLines = Int.MAX_VALUE)
    }
    Fact(title = "App version", value = LIBRARY_VERSION)
    ActionStack {
        PrimaryButton("Done", onClick = {})
        SecondaryButton("Reset", onClick = {})
        SecondaryButton("Locked", onClick = {}, enabled = false)
    }
}

// ---------------------------------------------------------------- Distinct

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DistinctSection(hueState: HueState) {
    SubSection(title = { UnfocusedText("Telling things"); FocusedText("apart") }) {
        Paragraph {
            +"Chart series, people and tags need colors that differ. Each one steps the hue by the golden angle, "
            +"137.5°, which spreads any number of them around the wheel. They use the primary's tone and get the same "
            +"contrast adjustment."
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        CodeHint("distinct(i) = adjust(hsl(h + i × 137.5°, S_primary, L_primary))   // distinct(0) = primary")
        ThemePreview(hueState) {
            val colors = TabotaboTheme.colors
            FlowRow(horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s), verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s)) {
                DistinctLabels.forEachIndexed { index, label ->
                    Tag("$label · ${hueState.hue.rotate(index * 137.5f).degrees.roundToInt()}°", color = colors.distinct(index))
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Material 3

@Composable
private fun MaterialSection() {
    SubSection(title = { UnfocusedText("Material 3"); FocusedText("mapping") }) {
        Paragraph { +"Stock Material components follow the scheme because every Material slot is filled from a role." }
        SpecTable(
            headers = listOf("Role", "Material 3 slots"),
            rows = listOf(
                listOf("primary", "primary, secondary, tertiary, inversePrimary, on…Container"),
                listOf("onPrimary", "onPrimary, onSecondary, onTertiary"),
                listOf("background", "background, inverseOnSurface"),
                listOf("surface", "surface, surfaceVariant, every surfaceContainer, every …Container"),
                listOf("accent", "onBackground, onSurface, inverseSurface"),
                listOf("muted", "onSurfaceVariant"),
                listOf("outlineStrong", "outline"),
                listOf("outline", "outlineVariant"),
                listOf("background at 60%", "scrim"),
                listOf("Material's own", "error, onError, errorContainer, onErrorContainer"),
            ),
            weights = listOf(1f, 3f),
        )
    }
}

// ---------------------------------------------------------------- Helpers

/** HSL lightness of this color, `(max + min) / 2` of its sRGB channels. */
private fun Color.hslLightness(): Float = (maxOf(red, green, blue) + minOf(red, green, blue)) / 2f

/** "#RRGGBB". */
private fun Color.hex(): String =
    "#" + listOf(red, green, blue).joinToString("") { (it * 255).roundToInt().toString(16).padStart(2, '0') }.uppercase()

/** A lightness: two decimals, or three when needed ("0.40", "0.995"). */
private fun Float.fmt(): String {
    val thousandths = (this * 1000).roundToInt()
    if (thousandths % 10 == 0) return toDouble().fmt()
    return "${thousandths / 1000}.${(thousandths % 1000).toString().padStart(3, '0')}"
}

/** Two decimals, e.g. "4.51". */
private fun Double.fmt(): String {
    val hundredths = (this * 100).roundToInt()
    return "${hundredths / 100}.${(hundredths % 100).toString().padStart(2, '0')}"
}
