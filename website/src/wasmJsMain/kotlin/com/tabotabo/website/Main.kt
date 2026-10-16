package com.tabotabo.website

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

/** Entry point: renders the website into the page. */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        WebsiteApp()
    }
}
