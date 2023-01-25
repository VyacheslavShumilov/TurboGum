package com.vshum.turbogum.navigator

import com.vshum.turbogum.model.Liner

enum class Screen {
    START_SCREEN,
    HELP_SCREEN,
    WRAPPERS_LIST_SCREEN,
    FAVOURITE
//    TURBO,
//    TURBO_2000,
//    TURBO_CLASSIC,
//    TURBO_LEGENDS,
//    TURBO_POWER,
//    TURBO_SPORT,
//    TURBO_SPORT_2003,
//    TURBO_SUPER_2003,
//    TURBO_SUPER_2007

}

interface AppNavigator {
    fun navigateTo(screen: Screen)
}

enum class ScreenParamWrapper {
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

interface AppNavigatorParamWrapper {
    fun navigateToParamWrapper(screen: ScreenParamWrapper, nameWrapper: String)
}

enum class ScreenParamLiner {
    TURBO
}

interface AppNavigatorParamLiner {
    fun navigateToParamLiner(screen: ScreenParamLiner, liner: Liner)
}