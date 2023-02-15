package com.vshum.turbogum.navigator

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.R
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.ui.HelpScreenFragment
import com.vshum.turbogum.ui.RegistrationFragment
import com.vshum.turbogum.ui.wrappers_list.WrappersListFragment
import com.vshum.turbogum.ui.StartScreenFragment
import com.vshum.turbogum.ui.favorite_liner.FavoriteLinerFragment
import com.vshum.turbogum.ui.favourite.FavouriteFragment
import com.vshum.turbogum.ui.liner.*
import com.vshum.turbogum.ui.liners_lists.LinersListFragment

class AppNavigatorImplFav(private var fragmentActivity: FragmentActivity) : AppNavigator,
    AppNavigatorParamWrapper, AppNavigatorParamLiner, AppNavigatorParamLinerFav {

    override fun navigateTo(screen: Screen) {
        val fragment = when (screen) {
            Screen.START_SCREEN -> StartScreenFragment()
            Screen.REGISTRATION_SCREEN -> RegistrationFragment()
            Screen.HELP_SCREEN -> HelpScreenFragment()
            Screen.WRAPPERS_LIST_SCREEN -> WrappersListFragment()
            Screen.FAVOURITE -> FavouriteFragment()
        }

        fragmentActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }

    override fun navigateToParamWrapper(screen: ScreenParamWrapper, series: String) {
        val fragment = when (screen) {
            ScreenParamWrapper.SERIES_1 -> LinersListFragment(series)
            ScreenParamWrapper.SERIES_2 -> LinersListFragment(series)
            ScreenParamWrapper.SERIES_3 -> LinersListFragment(series)
            ScreenParamWrapper.SERIES_4 -> LinersListFragment(series)
            ScreenParamWrapper.SERIES_5 -> LinersListFragment(series)
            ScreenParamWrapper.SUPER_1 -> LinersListFragment(series)
            ScreenParamWrapper.SUPER_2 -> LinersListFragment(series)
            ScreenParamWrapper.SUPER_3 -> LinersListFragment(series)
            ScreenParamWrapper.SPORT_1 -> LinersListFragment(series)
            ScreenParamWrapper.SPORT_2 -> LinersListFragment(series)
            ScreenParamWrapper.SPORT_3 -> LinersListFragment(series)
            ScreenParamWrapper.SPORT_4 -> LinersListFragment(series)
            ScreenParamWrapper.SPORT_5 -> LinersListFragment(series)
            ScreenParamWrapper.CLASSIC_1 -> LinersListFragment(series)
            ScreenParamWrapper.CLASSIC_2 -> LinersListFragment(series)
            ScreenParamWrapper.POWER -> LinersListFragment(series)
        }
        fragmentActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }


    override fun navigateToParamLiner(screen: ScreenParamLiner, liner: Liner) {
        val fragment = when (screen) {
            ScreenParamLiner.TURBO -> LinerFragment(liner)
        }

        fragmentActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }

    override fun navigateToParamLinerFav(screen: ScreenParamLinerFav, linerFav: LinersFavourite) {
        val fragment = when(screen) {
            ScreenParamLinerFav.FAVORITE_LINER -> FavoriteLinerFragment(linerFav)
        }
        fragmentActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }
}