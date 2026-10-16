@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tabotabo.website

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.tabotabo.ui.theme.SystemBarsAppearance

/*
 * The few browser calls the website needs, as small JS functions. Each one swallows its own
 * errors (e.g. storage blocked in a private window), so Kotlin never sees a browser exception.
 */

/** Reads [key] from localStorage, or null when absent or unavailable. */
@JsFun("(key) => { try { return window.localStorage.getItem(key); } catch (e) { return null; } }")
internal external fun storageGet(key: String): String?

/** Writes [value] under [key] in localStorage; null removes the key. A no-op when storage is unavailable. */
@JsFun(
    "(key, value) => { try { if (value === null) window.localStorage.removeItem(key); " +
        "else window.localStorage.setItem(key, value); } catch (e) {} }",
)
internal external fun storageSet(key: String, value: String?)

/** Sets the page's `<meta name="theme-color">`, which tints the browser UI on mobile. */
@JsFun("(color) => { const meta = document.querySelector('meta[name=\"theme-color\"]'); if (meta) meta.setAttribute('content', color); }")
internal external fun setThemeColorMeta(color: String)

/** Removes the static HTML shown while the app loads (and indexed by search engines). */
@JsFun("() => { const fallback = document.getElementById('fallback'); if (fallback) fallback.remove(); }")
internal external fun removeLoadingFallback()

/** This color as a CSS hex string, ignoring alpha. */
internal fun Color.toCssHex(): String = "#" + (toArgb() and 0xFFFFFF).toString(16).padStart(6, '0')

/** The website's system-bar hook: tints the browser chrome with the scheme's background. */
internal val BrowserThemeColor = SystemBarsAppearance { colors -> setThemeColorMeta(colors.background.toCssHex()) }
