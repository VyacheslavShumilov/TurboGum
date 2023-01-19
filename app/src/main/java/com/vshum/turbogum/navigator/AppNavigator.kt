package com.vshum.turbogum.navigator

enum class Screen {
    START_SCREEN,
    HELP_SCREEN,
    WRAPPERS_LIST_SCREEN
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
    TURBO_LINER
//    TURBO_2000_LINER,
//    TURBO_CLASSIC_LINER,
//    TURBO_LEGENDS_LINER,
//    TURBO_POWER_LINER,
//    TURBO_SPORT_LINER,
//    TURBO_SPORT_2003_LINER,
//    TURBO_SUPER_2003_LINER,
//    TURBO_SUPER_2007_LINER
}

interface AppNavigatorParamLiner {
    fun navigateToParamLiner(screen: ScreenParamLiner, idLiner: String)
}