package com.vshum.turbogum.services

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.navigator.*

class ServicesLocator {
    fun providerNavigator(fragmentActivity: FragmentActivity): AppNavigator {
        return AppNavigatorImplFav(fragmentActivity)
    }

    fun providerNavigatorParamWrapper(fragmentActivity: FragmentActivity): AppNavigatorParamWrapper {
        return AppNavigatorImplFav(fragmentActivity)
    }

    fun providerNavigatorParamLiners(fragmentActivity: FragmentActivity): AppNavigatorParamLiner {
        return AppNavigatorImplFav(fragmentActivity)
    }

    fun providerNavigatorParamFavLiner(fragmentActivity: FragmentActivity): AppNavigatorParamLinerFav {
        return AppNavigatorImplFav(fragmentActivity)
    }

}