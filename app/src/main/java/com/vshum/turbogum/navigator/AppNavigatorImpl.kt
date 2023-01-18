package com.vshum.turbogum.navigator

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.R
import com.vshum.turbogum.ui.HelpScreenFragment
import com.vshum.turbogum.ui.liners_list.LinersListFragment
import com.vshum.turbogum.ui.StartScreenFragment

class AppNavigatorImpl(private var fragmentActivity: FragmentActivity) : AppNavigator {
    override fun navigateTo(screen: Screen) {
        val fragment = when (screen) {
            Screen.START_SCREEN -> StartScreenFragment()
            Screen.HELP_SCREEN -> HelpScreenFragment()
            Screen.LINERS_LIST_SCREEN -> LinersListFragment()
//            Screen.TURBO -> TurboFragment()
//            Screen.TURBO_2000 -> Turbo2000Fragment()
//            Screen.TURBO_CLASSIC -> TurboClassicFragment()
//            Screen.TURBO_LEGENDS -> TurboLegendsFragment()
        }

        fragmentActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }
}