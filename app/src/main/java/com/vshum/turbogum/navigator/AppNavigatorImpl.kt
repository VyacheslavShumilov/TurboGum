package com.vshum.turbogum.navigator

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.R
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.ui.HelpScreenFragment
import com.vshum.turbogum.ui.wrappers_list.WrappersListFragment
import com.vshum.turbogum.ui.StartScreenFragment
import com.vshum.turbogum.ui.favourite.FavouriteFragment
import com.vshum.turbogum.ui.liner.*
import com.vshum.turbogum.ui.liners_lists.LinersListFragment

class AppNavigatorImpl(private var fragmentActivity: FragmentActivity) : AppNavigator,
    AppNavigatorParamWrapper, AppNavigatorParamLiner {
    override fun navigateTo(screen: Screen) {
        val fragment = when (screen) {
            Screen.START_SCREEN -> StartScreenFragment()
            Screen.HELP_SCREEN -> HelpScreenFragment()
            Screen.WRAPPERS_LIST_SCREEN -> WrappersListFragment()
            Screen.FAVOURITE -> FavouriteFragment()
        }

        fragmentActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }

    override fun navigateToParamWrapper(screen: ScreenParamWrapper, nameWrapper: String) {
        val fragment = when (screen) {
            ScreenParamWrapper.TURBO -> LinersListFragment(nameWrapper)
            ScreenParamWrapper.TURBO_2000 -> LinersListFragment(nameWrapper)
            ScreenParamWrapper.TURBO_CLASSIC -> LinersListFragment(nameWrapper)
            ScreenParamWrapper.TURBO_LEGENDS -> LinersListFragment(nameWrapper)
            ScreenParamWrapper.TURBO_POWER -> LinersListFragment(nameWrapper)
            ScreenParamWrapper.TURBO_SPORT -> LinersListFragment(nameWrapper)
            ScreenParamWrapper.TURBO_SPORT_2003 -> LinersListFragment(nameWrapper)
            ScreenParamWrapper.TURBO_SUPER_2003 -> LinersListFragment(nameWrapper)
            ScreenParamWrapper.TURBO_SUPER_2007 -> LinersListFragment(nameWrapper)
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
}