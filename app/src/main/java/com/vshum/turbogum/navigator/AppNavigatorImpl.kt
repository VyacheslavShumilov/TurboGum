package com.vshum.turbogum.navigator

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.R

class AppNavigatorImpl(private var fragmentActivity: FragmentActivity): AppNavigator {
    override fun navigateTo(screen: Screen) {
        val fragment = when(screen) {
            Screen.TURBO -> TurboFragment()
            Screen.TURBO_2000 -> Turbo2000Fragment()
            Screen.TURBO_CLASSIC -> TurboClassicFragment()
            Screen.TURBO_LEGENDS -> TurboLegendsFragment()

        }

        fragmentActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.orvListContainer, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }
}