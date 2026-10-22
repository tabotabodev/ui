package com.tabotabo.website.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.website.API_URL
import com.tabotabo.website.CodeHint
import com.tabotabo.website.GITHUB_URL
import com.tabotabo.website.INSTALL_LINE
import com.tabotabo.website.LIBRARY_VERSION
import com.tabotabo.website.LocalPhoneLayout
import com.tabotabo.website.Paragraph
import com.tabotabo.website.SHOWCASE_URL
import com.tabotabo.website.SitePage
import com.tabotabo.website.SitePageScaffold
import com.tabotabo.website.SubSection

private const val README_URL = "$GITHUB_URL#readme"

/** Get started: install the library, wrap the UI in the theme, teach Claude, and where to go next. */
@Composable
internal fun StartPage() {
    SitePageScaffold(page = SitePage.Start) {
        Paragraph {
            +"Tabotabo UI is published on Maven Central and runs on Android, iOS, desktop and the web. It also works in "
            +"plain Jetpack Compose apps."
        }
        InstallSection()
        ThemeSection()
        ClaudeSkillSection()
        NextSection()
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xl))
    }
}

@Composable
private fun InstallSection() {
    val spacing = TabotaboTheme.dimensions.spacing
    SubSection(title = { FocusedText("Install") }) {
        Paragraph { +"In a Kotlin Multiplatform project, add it to your shared module:" }
        Spacer(Modifier.height(spacing.m))
        CodeHint(
            """
            kotlin {
                sourceSets {
                    commonMain.dependencies {
                        $INSTALL_LINE
                    }
                }
            }
            """,
        )
        Paragraph { +"In an Android app, add it to the app module. Gradle picks the Android variant:" }
        Spacer(Modifier.height(spacing.m))
        CodeHint(
            """
            dependencies {
                $INSTALL_LINE
            }
            """,
        )
        Paragraph { +"Or through a version catalog:" }
        Spacer(Modifier.height(spacing.m))
        CodeHint("""tabotabo-ui = { module = "io.github.tabotabodev:tabotabo-ui", version = "$LIBRARY_VERSION" }""")
        Paragraph {
            +"It needs Android minSdk 29, Kotlin 2.4 and Compose Multiplatform 1.12. Compose runtime, foundation, UI "
            +"and Material 3 come with it."
        }
    }
}

@Composable
private fun ThemeSection() {
    SubSection(title = { UnfocusedText("Wrap your UI in the"); FocusedText("theme") }) {
        Paragraph {
            +"Your app decides light or dark; the library picks the colors. A new hue is chosen on every launch."
        }
        Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.m))
        CodeHint(
            """
            @Composable
            fun App() {
                val hueState = rememberHueState() // a fresh hue every launch
                TabotaboTheme(darkTheme = isSystemInDarkTheme(), hueState = hueState) {
                    HomeScreen()
                }
            }
            """,
        )
        Paragraph {
            +"The "
            link("README", README_URL)
            +" covers the rest: saving the appearance choice, tinting system bars, custom fonts, and every component."
        }
    }
}

@Composable
private fun ClaudeSkillSection() {
    val uriHandler = LocalUriHandler.current
    val spacing = TabotaboTheme.dimensions.spacing
    SubSection(title = { UnfocusedText("Teach"); FocusedText("Claude"); UnfocusedText("the system") }) {
        Paragraph {
            +"Tabotabo UI ships with a "
            bold("Claude skill")
            +": a short guide that teaches Claude how to set up the theme, which component fits which need, and the "
            +"design rules behind them. With it installed, asking Claude for \"a settings screen\" gives you one built from "
            +"the library, in the library's style."
        }
        Paragraph { bold("In Claude Code"); +", install it for every project on your machine:" }
        Spacer(Modifier.height(spacing.m))
        CodeHint(
            """
            mkdir -p ~/.claude/skills/tabotabo-ui
            curl -fsSL https://tabotabodev.github.io/ui/skill/SKILL.md \
              -o ~/.claude/skills/tabotabo-ui/SKILL.md
            """,
        )
        Paragraph {
            +"Or commit it to one repository under "
            bold(".claude/skills/tabotabo-ui/SKILL.md")
            +" so everyone on that project gets it."
        }
        Paragraph { bold("In the Claude apps"); +", download the skill as a zip and upload it from Claude's skills settings." }
        ActionStack(modifier = Modifier.padding(top = spacing.xl), maxWidth = 480.dp, alignment = Alignment.Start) {
            SecondaryButton("Download the skill", onClick = { uriHandler.openUri("skill/tabotabo-ui-skill.zip") })
        }
    }
}

/** Cards linking out of the site: showcase, API reference, README and source. */
@Composable
private fun NextSection() {
    val uriHandler = LocalUriHandler.current
    val spacing = TabotaboTheme.dimensions.spacing
    val destinations = listOf(
        Triple("Live showcase", "Every component in a running app.", SHOWCASE_URL),
        Triple("API reference", "Every type and parameter, generated from the source.", API_URL),
        Triple("README", "Installation, setup and usage for developers.", README_URL),
        Triple("Source", "Issues, releases and the code itself.", GITHUB_URL),
    )
    val columns = if (LocalPhoneLayout.current) 1 else 2
    SubSection(title = { UnfocusedText("Where to go"); FocusedText("next") }) {
        Column(Modifier.padding(top = spacing.l), verticalArrangement = Arrangement.spacedBy(spacing.m)) {
            destinations.chunked(columns).forEach { row ->
                Row(Modifier.height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(spacing.m)) {
                    row.forEach { (title, description, url) ->
                        GlowCard(modifier = Modifier.weight(1f).fillMaxHeight(), onClick = { uriHandler.openUri(url) }) {
                            Text(title, color = TabotaboTheme.colors.primary, style = TabotaboTheme.typography.labelLarge)
                            Subtitle(description, maxLines = Int.MAX_VALUE)
                        }
                    }
                    repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
