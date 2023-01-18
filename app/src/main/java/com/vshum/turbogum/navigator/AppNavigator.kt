package com.vshum.turbogum.navigator

enum class Screen {
    START_SCREEN,
    HELP_SCREEN,
    WRAPPERS_LIST_SCREEN,

//    TURBO,
//    TURBO_2000,
//    TURBO_CLASSIC,
//    TURBO_LEGENDS
}

interface AppNavigator {
    fun navigateTo(screen: Screen)
}