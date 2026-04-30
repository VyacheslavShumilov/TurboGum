package com.vshum.turbogum.navigator

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.R
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.ui.DevelopersFragment
import com.vshum.turbogum.ui.HelpScreenFragment
import com.vshum.turbogum.ui.RegistrationFragment
import com.vshum.turbogum.ui.StartScreenFragment
import com.vshum.turbogum.ui.community.CommunityFragment
import com.vshum.turbogum.ui.favorite_liner.FavoriteLinerFragment
import com.vshum.turbogum.ui.favourite_list.FavouriteListFragment
import com.vshum.turbogum.ui.liner.LinerFragment
import com.vshum.turbogum.ui.liners_lists.LinersListFragment
import com.vshum.turbogum.ui.profile.ProfileFragment
import com.vshum.turbogum.ui.scan.ScanFragment
import com.vshum.turbogum.ui.splash.SplashFragment
import com.vshum.turbogum.ui.wrappers_list.WrappersListFragment

class AppNavigatorImpl(private var fragmentActivity: FragmentActivity) :
    AppNavigator,
    AppNavigatorParamWrapper,
    AppNavigatorParamLiner,
    AppNavigatorParamLinerFav {

    override fun navigateTo(screen: Screen) {
        val fragment = when (screen) {
            Screen.START_SCREEN         -> StartScreenFragment()
            Screen.REGISTRATION_SCREEN  -> RegistrationFragment()
            Screen.HELP_SCREEN          -> HelpScreenFragment()
            Screen.WRAPPERS_LIST_SCREEN -> WrappersListFragment()
            Screen.FAVOURITE            -> FavouriteListFragment()
            Screen.DEVELOPERS_SCREEN    -> DevelopersFragment()
            Screen.SCAN_SCREEN          -> ScanFragment()
            Screen.COMMUNITY_SCREEN     -> CommunityFragment()
            Screen.PROFILE_SCREEN       -> ProfileFragment()
            Screen.SPLASH_SCREEN       -> SplashFragment()
        }
        fragmentActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }

    override fun navigateToParamWrapper(screen: ScreenParamWrapper, series: String) {
        val fragment = LinersListFragment(series)
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

    override fun navigateToParamLinerFav(
        screen: ScreenParamLinerFav,
        linerFav: LinersFavourite
    ) {
        val fragment = when (screen) {
            ScreenParamLinerFav.FAVORITE_LINER -> FavoriteLinerFragment(linerFav)
        }
        fragmentActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }
}