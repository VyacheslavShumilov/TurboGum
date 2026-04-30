package com.vshum.turbogum.navigator

import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.model.LinersFavourite

/**
 * Screen catalog. All destinations the app can navigate to.
 */
enum class Screen {
    SPLASH_SCREEN,
    WRAPPERS_LIST_SCREEN,
    LINERS_LIST_SCREEN,
    LINER_SCREEN,
    FAVOURITE,
    FAVOURITE_LINER_SCREEN,
    PROFILE_SCREEN,
    DEVELOPERS_SCREEN,
    REGISTRATION,
    HELP
}

/** Navigate to a screen with no parameters. */
interface AppNavigator {
    fun navigateTo(screen: Screen)
}

/** Navigate to a screen with a wrapper/series key parameter (e.g. "series1"). */
interface AppNavigatorParamWrapper {
    fun navigateToParamWrapper(screen: Screen, seriesKey: String)
}

/** Navigate to a screen with a Liner model parameter. */
interface AppNavigatorParamLiners {
    fun navigateToParamLiner(screen: Screen, liner: Liner)
}

/** Navigate to a screen with a favourite-liner model parameter. */
interface AppNavigatorParamLinerFav {
    fun navigateToParamLinerFav(screen: Screen, linerFav: LinersFavourite)
}