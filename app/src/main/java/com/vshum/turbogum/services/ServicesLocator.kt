package com.vshum.turbogum.services

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorImpl
import com.vshum.turbogum.navigator.AppNavigatorParamLiner
import com.vshum.turbogum.navigator.AppNavigatorParamWrapper

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

}