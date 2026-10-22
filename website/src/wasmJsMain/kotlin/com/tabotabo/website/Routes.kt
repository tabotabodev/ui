package com.tabotabo.website

import androidx.compose.runtime.staticCompositionLocalOf
import com.tabotabo.ui.text.EmphasisTextScope

/** The site's name, appended to every page's browser-tab title. */
internal const val SITE_NAME = "Tabotabo UI"

/** Latest published library version, shown in install snippets. Update after each release. */
internal const val LIBRARY_VERSION = "0.2.1"

/** The Gradle line that adds the library. */
internal const val INSTALL_LINE = """implementation("io.github.tabotabodev:tabotabo-ui:$LIBRARY_VERSION")"""

/**
 * Every page of the website.
 *
 * @property route Navigation route, as registered with the `NavHost`.
 * @property path Address under the site root, with a trailing slash ("" for home). Keep the
 *   pages, titles and descriptions in sync with `PAGES` in `scripts/route_pages.py`, which writes
 *   each page's index.html for GitHub Pages.
 * @property navLabel Short name for links, buttons and the breadcrumb.
 * @property description One sentence for the meta description and link previews.
 * @property subtitle Muted line under the page title.
 * @property parent The section this page belongs to, shown in the breadcrumb.
 * @property title The page title: plain words and the key phrase.
 */
internal enum class SitePage(
    val route: String,
    val path: String,
    val navLabel: String,
    val description: String,
    val subtitle: String,
    val parent: SitePage? = null,
    val title: EmphasisTextScope.() -> Unit,
) {
    Home(
        route = "home",
        path = "",
        navLabel = "Home",
        description = "Tabotabo UI is a minimal, editorial design system for Compose Multiplatform: one random hue, " +
            "Montserrat type, outlined surfaces and quiet motion, on Android, iOS, desktop and the web.",
        subtitle = "One look for every app, from a single hue.",
        title = { UnfocusedText("Tabotabo"); FocusedText("UI") },
    ),
    Principles(
        route = "principles",
        path = "principles/",
        navLabel = "Principles",
        description = "The ideas behind Tabotabo UI: one hue, one type family, outlined surfaces, calm layouts and quiet motion.",
        subtitle = "Five ideas that every screen follows.",
        title = { UnfocusedText("What makes it"); FocusedText("Tabotabo") },
    ),
    Color(
        route = "principles/color",
        path = "principles/color/",
        navLabel = "Color",
        description = "How every Tabotabo UI color is derived from a single hue, with a live preview.",
        subtitle = "Every role comes from one hue.",
        parent = Principles,
        title = { UnfocusedText("One hue,"); FocusedText("every color") },
    ),
    Text(
        route = "principles/text",
        path = "principles/text/",
        navLabel = "Text",
        description = "Tabotabo UI's type scale and its Light + Bold emphasis rule, with a live playground.",
        subtitle = "Montserrat only. Weight and color create hierarchy.",
        parent = Principles,
        title = { UnfocusedText("One family,"); FocusedText("weight and color") },
    ),
    Surfaces(
        route = "principles/surfaces",
        path = "principles/surfaces/",
        navLabel = "Surfaces",
        description = "Cards, buttons, pills and dialogs in Tabotabo UI: outlined, never elevated.",
        subtitle = "Glow is for the one thing that needs attention.",
        parent = Principles,
        title = { UnfocusedText("Outlined, not"); FocusedText("elevated") },
    ),
    Layout(
        route = "principles/layout",
        path = "principles/layout/",
        navLabel = "Layout",
        description = "Tabotabo UI's layouts: left-aligned content, a fixed spacing scale and no layout shifts.",
        subtitle = "Left-aligned, calm spacing, nothing jumps.",
        parent = Principles,
        title = { UnfocusedText("Calm"); FocusedText("layouts") },
    ),
    Motion(
        route = "principles/motion",
        path = "principles/motion/",
        navLabel = "Motion",
        description = "Motion in Tabotabo UI: typewriter headlines, pop-ins, pulses and the occasional celebration.",
        subtitle = "Expected, but quiet.",
        parent = Principles,
        title = { UnfocusedText("Purposeful"); FocusedText("motion") },
    ),
    Components(
        route = "components",
        path = "components/",
        navLabel = "Components",
        description = "A live tour of Tabotabo UI's components: buttons, glow cards, pills, dialogs, layouts and more.",
        subtitle = "Every example is the real component, running live.",
        title = { UnfocusedText("A"); FocusedText("quick tour") },
    ),
    Start(
        route = "start",
        path = "start/",
        navLabel = "Get started",
        description = "Add Tabotabo UI to a Compose Multiplatform or Android app, and teach Claude the system.",
        subtitle = "From Maven Central to your first themed screen.",
        title = { UnfocusedText("Add it in"); FocusedText("one line") },
    ),
    NotFound(
        route = "not-found",
        path = "",
        navLabel = "Not found",
        description = "This page doesn't exist.",
        subtitle = "The link may be old, or the address mistyped.",
        title = { UnfocusedText("Page not"); FocusedText("found") },
    ),
    ;

    /** Browser-tab title: the page name and the site name, or just the site name on home. */
    val documentTitle: String
        get() = if (this == Home) "$SITE_NAME · A Compose Multiplatform design system" else "$navLabel · $SITE_NAME"

    /** The header link this page counts as: its parent section, or itself. */
    val section: SitePage
        get() = parent ?: this

    companion object {
        /** Links in the site header, in order. */
        val HeaderLinks: List<SitePage> = listOf(Principles, Components, Start)

        /** The page registered under [route], or [NotFound]. */
        fun fromRoute(route: String?): SitePage = entries.firstOrNull { it.route == route } ?: NotFound

        /**
         * The page at [path] relative to the site root ("principles/color/"). Leading and missing
         * trailing slashes and a trailing "index.html" are tolerated. Unknown paths give [NotFound].
         */
        fun fromPath(path: String): SitePage {
            val normalised = path.trimStart('/').removeSuffix("index.html").let { if (it.isEmpty() || it.endsWith("/")) it else "$it/" }
            return entries.firstOrNull { it != NotFound && it.path == normalised } ?: NotFound
        }
    }
}

/** Opens a page of the site inside the app (with a browser history entry). */
internal val LocalNavigate = staticCompositionLocalOf<(SitePage) -> Unit> { {} }

/** Opens the settings dialog. */
internal val LocalOpenSettings = staticCompositionLocalOf<() -> Unit> { {} }

/** Whether the site is laid out for a phone (narrower than [PhoneMaxWidth]). */
internal val LocalPhoneLayout = staticCompositionLocalOf { false }
