package com.tabotabo.ui

interface Platform {
    val name: String

    /** Whether the app runs in a browser, where screens use the single-column web layout. */
    val isWeb: Boolean get() = false
}

expect fun getPlatform(): Platform