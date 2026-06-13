package com.vshum.turbogum

import androidx.fragment.app.FragmentActivity
import com.vshum.turbogum.data.AuthRepository
import com.vshum.turbogum.data.UserRepository
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorImpl
import com.vshum.turbogum.navigator.AppNavigatorParamLinerFav
import com.vshum.turbogum.navigator.AppNavigatorParamLiners
import com.vshum.turbogum.navigator.AppNavigatorParamWrapper

/**
 * Single-entry factory for the navigation services.
 * The same [AppNavigatorImpl] instance implements all four navigator
 * interfaces, so each of the `provider*` methods returns the same
 * underlying object cast to the requested interface.
 */
class ServicesLocator {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    fun providerNavigator(fragmentActivity: FragmentActivity): AppNavigator =
        AppNavigatorImpl(fragmentActivity)

    fun providerNavigatorParamWrapper(fragmentActivity: FragmentActivity): AppNavigatorParamWrapper =
        AppNavigatorImpl(fragmentActivity)

    fun providerNavigatorParamLiners(fragmentActivity: FragmentActivity): AppNavigatorParamLiners =
        AppNavigatorImpl(fragmentActivity)

    fun providerNavigatorParamLinerFav(fragmentActivity: FragmentActivity): AppNavigatorParamLinerFav =
        AppNavigatorImpl(fragmentActivity)

    fun providerAuthRepository(): AuthRepository = authRepository

    fun providerUserRepository(): UserRepository = userRepository
}