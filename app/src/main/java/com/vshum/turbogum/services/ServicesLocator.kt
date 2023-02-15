package com.vshum.turbogum.services

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.navigator.*

class ServicesLocator {
    fun providerNavigator(fragmentActivity: FragmentActivity): AppNavigator {
        return AppNavigatorImpl(fragmentActivity)
    }

    fun providerNavigatorParamWrapper(fragmentActivity: FragmentActivity): AppNavigatorParamWrapper {
        return AppNavigatorImpl(fragmentActivity)
    }

    fun providerNavigatorParamLiners(fragmentActivity: FragmentActivity): AppNavigatorParamLiner {
        return AppNavigatorImpl(fragmentActivity)
    }

    fun providerNavigatorParamLinerFav(fragmentActivity: FragmentActivity): AppNavigatorParamLinerFav {
        return AppNavigatorImpl(fragmentActivity)
    }

}