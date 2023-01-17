package com.vshum.turbogum.navigator

enum class Screen {
    TURBO,
    TURBO_2000,
    TURBO_CLASSIC,
    TURBO_LEGENDS
}

interface AppNavigator {
    fun navigateTo(screen: Screen)
}