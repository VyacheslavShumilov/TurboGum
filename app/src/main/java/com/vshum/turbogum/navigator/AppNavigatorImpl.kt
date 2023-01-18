package com.vshum.turbogum.navigator

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.R
import com.vshum.turbogum.ui.HelpScreenFragment
import com.vshum.turbogum.ui.wrappers_list.WrappersListFragment
import com.vshum.turbogum.ui.StartScreenFragment
import com.vshum.turbogum.ui.liners_lists.*

class AppNavigatorImpl(private var fragmentActivity: FragmentActivity) : AppNavigator {
    override fun navigateTo(screen: Screen) {
        val fragment = when (screen) {
            Screen.START_SCREEN -> StartScreenFragment()
            Screen.HELP_SCREEN -> HelpScreenFragment()
            Screen.WRAPPERS_LIST_SCREEN -> WrappersListFragment()
            Screen.TURBO -> TurboFragment()
            Screen.TURBO_2000 -> Turbo2000Fragment()
            Screen.TURBO_CLASSIC -> TurboClassicFragment()
            Screen.TURBO_LEGENDS -> TurboLegendsFragment()
            Screen.TURBO_POWER -> TurboPowerFragment()
            Screen.TURBO_SPORT -> TurboSportFragment()
            Screen.TURBO_SPORT_2003 -> TurboSport2003Fragment()
            Screen.TURBO_SUPER_2003 -> TurboSuper2003Fragment()
            Screen.TURBO_SUPER_2007 -> TurboSuper2007Fragment()
        }

        fragmentActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }
}