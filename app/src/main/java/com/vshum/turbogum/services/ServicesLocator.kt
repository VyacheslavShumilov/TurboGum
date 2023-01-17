package com.vshum.turbogum.services

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorImpl

class ServicesLocator {
    fun providerNavigator(fragmentActivity: FragmentActivity): AppNavigator {
        return AppNavigatorImpl(fragmentActivity)
    }

}