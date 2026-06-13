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
    PROFILE_SCREEN,
    NOTES_SCREEN,
    DEVELOPERS_SCREEN,
    REGISTRATION,
    HELP,
    LOGIN_SCREEN,
    NICKNAME_SCREEN,
    LEADERBOARD_SCREEN
}

interface AppNavigator {
    fun navigateTo(screen: Screen)
}

interface AppNavigatorParamWrapper {
    fun navigateToParamWrapper(screen: Screen, seriesKey: String)
}

interface AppNavigatorParamLiners {
    fun navigateToParamLiner(screen: Screen, liner: Liner)
}

interface AppNavigatorParamLinerFav {
    fun navigateToParamLinerFav(screen: Screen, linerFav: LinersFavourite)
}