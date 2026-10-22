package com.tabotabo.ui.typography

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tabotabo.ui.resources.Res
import com.tabotabo.ui.resources.montserrat_black
import com.tabotabo.ui.resources.montserrat_bold
import com.tabotabo.ui.resources.montserrat_light
import com.tabotabo.ui.resources.montserrat_medium
import com.tabotabo.ui.resources.montserrat_regular
import org.jetbrains.compose.resources.Font

/**
 * The bundled Montserrat family with Light (300), Regular (400), Medium (500), Bold (700) and
 * Black (900). It is `@Composable` because Compose Resources loads fonts in composition.
 */
@Composable
public fun montserratFontFamily(): FontFamily = FontFamily(
    Font(Res.font.montserrat_light, FontWeight.Light),
    Font(Res.font.montserrat_regular, FontWeight.Normal),
    Font(Res.font.montserrat_medium, FontWeight.Medium),
    Font(Res.font.montserrat_bold, FontWeight.Bold),
    Font(Res.font.montserrat_black, FontWeight.Black),
)

/**
 * M3 [Typography] in Montserrat with the design system's sizes (see [tabotaboTypography]).
 */
@Composable
public fun montserratTypography(): Typography = tabotaboTypography(montserratFontFamily())

/**
 * The design system's type scale in any font. Each group can use its own family, so consumers
 * can bring their own fonts. Roles the design system doesn't define keep their Material sizes,
 * re-familied. The large and medium display and headline roles (`displayLarge`, `displayMedium`,
 * `headlineLarge`, `headlineMedium`) are Light, so emphasis text reads as Light + Bold.
 *
 * @param displayFamily Hero, headline and screen-title roles.
 * @param bodyFamily Body, caption and footnote roles.
 * @param labelFamily Buttons, labels and tags.
 */
public fun tabotaboTypography(
    displayFamily: FontFamily,
    bodyFamily: FontFamily = displayFamily,
    labelFamily: FontFamily = bodyFamily,
): Typography {
    val material = Typography()
    fun TextStyle.styled(family: FontFamily, size: TextUnit, weight: FontWeight, lineHeight: TextUnit) =
        copy(fontFamily = family, fontSize = size, fontWeight = weight, lineHeight = lineHeight, letterSpacing = 0.sp)
    fun TextStyle.refamilied(family: FontFamily) = copy(fontFamily = family)

    return Typography(
        displayLarge = material.displayLarge.styled(displayFamily, 42.sp, FontWeight.Light, 50.sp)
            .copy(lineBreak = LineBreak.Heading),
        displayMedium = material.displayMedium.styled(displayFamily, 34.sp, FontWeight.Light, 42.sp)
            .copy(lineBreak = LineBreak.Heading),
        displaySmall = material.displaySmall.refamilied(displayFamily),
        headlineLarge = material.headlineLarge.styled(displayFamily, 32.sp, FontWeight.Light, 40.sp)
            .copy(lineBreak = LineBreak.Heading),
        headlineMedium = material.headlineMedium.refamilied(displayFamily).copy(fontWeight = FontWeight.Light),
        headlineSmall = material.headlineSmall.refamilied(displayFamily),
        titleLarge = material.titleLarge.refamilied(displayFamily),
        titleMedium = material.titleMedium.refamilied(bodyFamily),
        titleSmall = material.titleSmall.styled(bodyFamily, 14.sp, FontWeight.Medium, 20.sp),
        bodyLarge = material.bodyLarge.refamilied(bodyFamily),
        bodyMedium = material.bodyMedium.styled(bodyFamily, 14.sp, FontWeight.Normal, 20.sp),
        bodySmall = material.bodySmall.styled(bodyFamily, 13.sp, FontWeight.Normal, 18.sp),
        labelLarge = material.labelLarge.styled(labelFamily, 16.sp, FontWeight.Bold, 22.sp),
        labelMedium = material.labelMedium.styled(labelFamily, 13.sp, FontWeight.Bold, 18.sp),
        labelSmall = material.labelSmall.styled(bodyFamily, 11.sp, FontWeight.Normal, 16.sp),
    )
}

/** Hero title (42sp Light, balanced line breaks), backed by `displayLarge`. */
public val Typography.heroTitle: TextStyle get() = displayLarge

/** Headline (34sp Light, balanced line breaks), backed by `displayMedium`. */
public val Typography.headline: TextStyle get() = displayMedium

/** Screen title (32sp Light, balanced line breaks), backed by `headlineLarge`. */
public val Typography.screenTitle: TextStyle get() = headlineLarge

/** Body text (14sp Regular), backed by `bodyMedium`. */
public val Typography.body: TextStyle get() = bodyMedium

/** Caption (13sp Regular), backed by `bodySmall`. */
public val Typography.caption: TextStyle get() = bodySmall

/** Label (13sp Bold), backed by `labelMedium`. */
public val Typography.label: TextStyle get() = labelMedium

/** Button label (16sp Bold), backed by `labelLarge`. */
public val Typography.buttonLabel: TextStyle get() = labelLarge

/** Footnote (11sp Regular), backed by `labelSmall`. */
public val Typography.footnote: TextStyle get() = labelSmall

/** Title of a title/value fact (14sp Medium), backed by `titleSmall`. */
public val Typography.factTitle: TextStyle get() = titleSmall

/**
 * Font weights used by emphasis text. Every hero, headline and title uses Light + Bold by default.
 *
 * @property unfocused Weight of the baseline words.
 * @property focused Weight of the key phrase.
 */
@Immutable
public data class EmphasisWeights(
    val unfocused: FontWeight = FontWeight.Light,
    val focused: FontWeight = FontWeight.Bold,
) {
    public companion object {
        /** Screen and dialog titles: Light + Bold. Same weights as [Headline]. */
        public val Title: EmphasisWeights = EmphasisWeights()

        /** Heroes and headlines (default for emphasis and typewriter text): Light + Bold. */
        public val Headline: EmphasisWeights = EmphasisWeights()
    }
}
