package com.vshum.turbogum.navigator

import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.model.LinersFavourite

enum class Screen {
    SPLASH_SCREEN,
    WRAPPERS_LIST_SCREEN,
    LINERS_LIST_SCREEN,
    LINER_SCREEN,
    FAVOURITE,
    FAVOURITE_LINER_SCREEN,
    DEVELOPERS_SCREEN,
    REGISTRATION,
    HELP,
    PROFILE_SCREEN
}

interface AppNavigator {
    fun navigateTo(screen: Screen)
}

enum class ScreenParamWrapper {
    SERIES_1,
    SERIES_2,
    SERIES_3,
    SERIES_4,
    SERIES_5,
    SUPER_1,
    SUPER_2,
    SUPER_3,
    SPORT_1,
    SPORT_2,
    CLASSIC_1,
    CLASSIC_2,
}

interface AppNavigatorParamWrapper {
    fun navigateToParamWrapper(screen: ScreenParamWrapper, series: String)
}

enum class ScreenParamLiner { TURBO }
interface AppNavigatorParamLiner {
    fun navigateToParamLiner(screen: ScreenParamLiner, liner: Liner)
}

enum class ScreenParamLinerFav { FAVORITE_LINER }
interface AppNavigatorParamLinerFav {
    fun navigateToParamLinerFav(screen: ScreenParamLinerFav, linerFav: LinersFavourite)
}