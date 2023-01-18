package com.vshum.turbogum.services

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorImpl
import com.vshum.turbogum.navigator.AppNavigatorParam

class ServicesLocator {
    fun providerNavigator(fragmentActivity: FragmentActivity): AppNavigator {
        return AppNavigatorImpl(fragmentActivity)
    }
    fun providerNavigatorParam(fragmentActivity: FragmentActivity): AppNavigatorParam {
        return AppNavigatorImpl(fragmentActivity)
    }

}