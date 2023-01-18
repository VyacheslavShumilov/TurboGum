package com.vshum.turbogum.navigator

enum class Screen {
    START_SCREEN,
    HELP_SCREEN,
    WRAPPERS_LIST_SCREEN,
    TURBO,
    TURBO_2000,
    TURBO_CLASSIC,
    TURBO_LEGENDS,
    TURBO_POWER,
    TURBO_SPORT,
    TURBO_SPORT_2003,
    TURBO_SUPER_2003,
    TURBO_SUPER_2007
}

interface AppNavigator {
    fun navigateTo(screen: Screen)
}