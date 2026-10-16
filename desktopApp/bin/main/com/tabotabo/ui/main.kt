package com.tabotabo.ui

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import javax.imageio.ImageIO

/** The mascot window icon, bundled from src/main/resources (see branding/generate_icons.py). */
private val WindowIcon = BitmapPainter(
    ImageIO.read(checkNotNull(Thread.currentThread().contextClassLoader.getResource("icon.png"))).toComposeImageBitmap(),
)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Tabotaboui",
        icon = WindowIcon,
    ) {
        App()
    }
}
