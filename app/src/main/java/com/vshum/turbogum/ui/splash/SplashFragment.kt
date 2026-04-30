package com.vshum.turbogum.ui.splash

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen

/**
 * Splash screen — shown briefly on app launch.
 * Renders BarsLogo, Turbo wordmark and version, then navigates to Home.
 */
class SplashFragment : Fragment() {

    private lateinit var appNavigator: AppNavigator
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler.postDelayed({
            if (isAdded) appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
        }, 1500L)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }
}