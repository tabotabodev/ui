package com.tabotabo.website

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.color.rememberHueState
import com.tabotabo.ui.settings.AppearanceSetting
import com.tabotabo.ui.settings.SettingsSection
import com.tabotabo.ui.settings.ShuffleColorsButton
import com.tabotabo.ui.surface.TabotaboDialog
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.website.pages.ComponentsPage
import com.tabotabo.website.pages.HomePage
import com.tabotabo.website.pages.NotFoundPage
import com.tabotabo.website.pages.StartPage
import com.tabotabo.website.pages.principles.ColorPage
import com.tabotabo.website.pages.principles.LayoutPage
import com.tabotabo.website.pages.principles.MotionPage
import com.tabotabo.website.pages.principles.PrinciplesPage
import com.tabotabo.website.pages.principles.SurfacesPage
import com.tabotabo.website.pages.principles.TextPage

/** The viewer's appearance choice; persisted per browser, unlike the hue. */
internal enum class Appearance(val label: String, val storageValue: String?) {
    Light("Light", "light"),
    Dark("Dark", "dark"),
    System("System", null),
}

private const val APPEARANCE_KEY = "tabotabo-appearance"

/** Duration of the fade between pages. */
private const val PAGE_FADE_MILLIS = 200

/**
 * Where the visitor landed, read once at start-up.
 *
 * @property sitePath Path of the site root, e.g. "/ui/" on GitHub Pages or "/" in development.
 * @property initialPage The page for the address the visitor opened.
 * @property initialPath That address relative to [sitePath]; kept as-is for [SitePage.NotFound].
 */
private class Landing(val sitePath: String, val initialPage: SitePage, val initialPath: String) {
    companion object {
        /** Reads the current address against the page's `<base href>`. */
        fun read(): Landing {
            val sitePath = sitePathFromBase().let { if (it.endsWith("/")) it else "$it/" }
            val pathname = currentPathname()
            val relative = if (pathname.startsWith(sitePath)) pathname.removePrefix(sitePath) else pathname.trimStart('/')
            return Landing(sitePath, SitePage.fromPath(relative), relative)
        }
    }
}

/**
 * Website root: a fresh hue on every page load, the viewer's saved appearance, the browser's
 * theme color kept in sync, the phone or wide layout, and one route per page, kept in sync with
 * the address bar and browser history.
 */
@OptIn(ExperimentalBrowserHistoryApi::class)
@Composable
internal fun WebsiteApp() {
    val landing = remember { Landing.read() }
    var appearance by remember {
        mutableStateOf(Appearance.entries.firstOrNull { it.storageValue == storageGet(APPEARANCE_KEY) } ?: Appearance.System)
    }
    val hueState = rememberHueState()
    var settingsOpen by remember { mutableStateOf(false) }
    val darkTheme = when (appearance) {
        Appearance.Light -> false
        Appearance.Dark -> true
        Appearance.System -> isSystemInDarkTheme()
    }
    val onAppearanceChange: (Appearance) -> Unit = {
        appearance = it
        storageSet(APPEARANCE_KEY, it.storageValue)
    }
    val navController = rememberNavController()
    val previewMode = remember { PreviewModeState() }

    LaunchedEffect(Unit) { removeLoadingFallback() }

    // The browser binding builds every address as "<address at bind time> + route path", so the
    // address is reset to the site root first; the binding then restores the landing page's path.
    LaunchedEffect(navController) {
        replaceAddress(landing.sitePath)
        navController.bindToBrowserNavigation { entry ->
            val page = SitePage.fromRoute(entry.destination.route)
            if (page == SitePage.NotFound) landing.initialPath else page.path
        }
    }

    val currentEntry by navController.currentBackStackEntryAsState()
    val currentPage = SitePage.fromRoute(currentEntry?.destination?.route ?: landing.initialPage.route)
    LaunchedEffect(currentPage) {
        setDocumentTitle(currentPage.documentTitle)
        setMetaDescription(currentPage.description)
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val phone = maxWidth < PhoneMaxWidth
        TabotaboTheme(
            darkTheme = darkTheme,
            hueState = hueState,
            typography = siteTypography(phone),
            systemBars = BrowserThemeColor,
        ) {
            CompositionLocalProvider(
                LocalNavigate provides { page -> navController.navigate(page.route) { launchSingleTop = true } },
                LocalOpenSettings provides { settingsOpen = true },
                LocalPhoneLayout provides phone,
                LocalPreviewMode provides previewMode,
            ) {
                NavHost(
                    navController = navController,
                    startDestination = landing.initialPage.route,
                    enterTransition = { fadeIn(tween(PAGE_FADE_MILLIS)) },
                    exitTransition = { fadeOut(tween(PAGE_FADE_MILLIS)) },
                ) {
                    SitePage.entries.forEach { page ->
                        composable(page.route) { PageContent(page, hueState, appearance, onAppearanceChange) }
                    }
                }
            }

            if (settingsOpen) {
                SettingsDialog(
                    appearance = appearance,
                    onAppearanceChange = onAppearanceChange,
                    hueState = hueState,
                    onClose = { settingsOpen = false },
                )
            }
        }
    }
}

/** The body of [page]. */
@Composable
private fun PageContent(
    page: SitePage,
    hueState: HueState,
    appearance: Appearance,
    onAppearanceChange: (Appearance) -> Unit,
) {
    when (page) {
        SitePage.Home -> HomePage()
        SitePage.Principles -> PrinciplesPage()
        SitePage.Color -> ColorPage(hueState)
        SitePage.Text -> TextPage(hueState)
        SitePage.Surfaces -> SurfacesPage(hueState)
        SitePage.Layout -> LayoutPage()
        SitePage.Motion -> MotionPage()
        SitePage.Components -> ComponentsPage(hueState, appearance, onAppearanceChange)
        SitePage.Start -> StartPage()
        SitePage.NotFound -> NotFoundPage()
    }
}

/** Appearance and color settings, opened from the header gear. */
@Composable
private fun SettingsDialog(
    appearance: Appearance,
    onAppearanceChange: (Appearance) -> Unit,
    hueState: HueState,
    onClose: () -> Unit,
) {
    TabotaboDialog(
        onDismissRequest = onClose,
        confirmText = "Done",
        onConfirm = onClose,
        title = { FocusedText("Settings") },
    ) {
        Column(Modifier.fillMaxWidth()) {
            AppearanceControl(appearance, onAppearanceChange)
            Spacer(Modifier.height(TabotaboTheme.dimensions.spacing.xl))
            SettingsSection(
                title = "Colors",
                description = "Every visit picks a new color. Shuffle to try another one now.",
            ) {
                ShuffleColorsButton(hueState = hueState, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/** The appearance picker, shared by the settings dialog and the Components page's settings example. */
@Composable
internal fun AppearanceControl(appearance: Appearance, onAppearanceChange: (Appearance) -> Unit) {
    AppearanceSetting(
        options = Appearance.entries,
        selected = appearance,
        onSelect = onAppearanceChange,
        label = { it.label },
        title = "Appearance",
        description = "Pick light or dark, or follow your device.",
    )
}
