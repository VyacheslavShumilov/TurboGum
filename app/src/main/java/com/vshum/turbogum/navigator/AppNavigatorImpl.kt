package com.vshum.turbogum.navigator

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.R
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.ui.DevelopersFragment
import com.vshum.turbogum.ui.HelpScreenFragment
import com.vshum.turbogum.ui.RegistrationFragment
import com.vshum.turbogum.ui.favorite_liner.FavoriteLinerFragment
import com.vshum.turbogum.ui.favourite_list.FavouriteListFragment
import com.vshum.turbogum.ui.liner.LinerFragment
import com.vshum.turbogum.ui.liners_lists.LinersListFragment
import com.vshum.turbogum.ui.profile.ProfileFragment
import com.vshum.turbogum.ui.splash.SplashFragment
import com.vshum.turbogum.ui.wrappers_list.WrappersListFragment

/**
 * Single concrete navigator handling parameterless and parameterised
 * navigation calls. Uses FragmentManager#replace into the host
 * fragmentContainer (R.id.fragmentContainer in activity_main.xml).
 */
class AppNavigatorImpl(private val activity: FragmentActivity) :
    AppNavigator,
    AppNavigatorParamWrapper,
    AppNavigatorParamLiners,
    AppNavigatorParamLinerFav {

    private val fm = activity.supportFragmentManager

    // ── Plain navigation ──────────────────────────────────────────────

    override fun navigateTo(screen: Screen) {
        val fragment = when (screen) {
            Screen.SPLASH_SCREEN -> SplashFragment()
            Screen.WRAPPERS_LIST_SCREEN -> WrappersListFragment()
            Screen.LINERS_LIST_SCREEN -> LinersListFragment("series1") // default
            Screen.FAVOURITE -> FavouriteListFragment()
            Screen.PROFILE_SCREEN -> ProfileFragment()
            Screen.DEVELOPERS_SCREEN -> DevelopersFragment()
            Screen.REGISTRATION -> RegistrationFragment()
            Screen.HELP -> HelpScreenFragment()
            // Param-required screens fall back to home if called without params
            Screen.LINER_SCREEN, Screen.FAVOURITE_LINER_SCREEN ->
                WrappersListFragment()
        }
        fm.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // ── Wrapper / series key parameter ────────────────────────────────

    override fun navigateToParamWrapper(screen: Screen, seriesKey: String) {
        val fragment = when (screen) {
            Screen.LINERS_LIST_SCREEN -> LinersListFragment(seriesKey)
            else -> WrappersListFragment()
        }
        fm.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // ── Liner parameter ───────────────────────────────────────────────

    override fun navigateToParamLiner(screen: Screen, liner: Liner) {
        val fragment = when (screen) {
            Screen.LINER_SCREEN -> LinerFragment(liner)
            else -> WrappersListFragment()
        }
        fm.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // ── Favourite-liner parameter ─────────────────────────────────────

    override fun navigateToParamLinerFav(screen: Screen, linerFav: LinersFavourite) {
        val fragment = when (screen) {
            Screen.FAVOURITE_LINER_SCREEN -> FavoriteLinerFragment(linerFav)
            else -> FavouriteListFragment()
        }
        fm.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}