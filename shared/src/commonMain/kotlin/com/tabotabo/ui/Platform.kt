package com.tabotabo.ui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform