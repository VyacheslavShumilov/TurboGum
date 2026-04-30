package com.vshum.turbogum

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vshum.turbogum.databinding.ActivityMainBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen

/**
 * Hosts all fragments and the bottom navigation.
 * - Starts with SplashFragment which auto-navigates to Home after a short delay.
 * - Bottom nav has 4 tabs (Home / Collection / Favorites / Profile);
 *   Collection re-opens the wrappers list (acts as a synonym for Home for now).
 * - Provides activity-level imageOverlay/expandedImage views for fullscreen
 *   image preview in LinerFragment.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appNavigator: AppNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appNavigator = (applicationContext as App).servicesLocator.providerNavigator(this)

        setupBottomNav()

        if (savedInstanceState == null) {
            // First launch — go through splash
            appNavigator.navigateTo(Screen.SPLASH_SCREEN)
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
                    true
                }
                R.id.nav_collection -> {
                    appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
                    true
                }
//                R.id.nav_favorites -> {
//                    appNavigator.navigateTo(Screen.FAVOURITE)
//                    true
//                }
                R.id.nav_profile -> {
                    appNavigator.navigateTo(Screen.PROFILE_SCREEN)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Show / hide the bottom nav. Splash screen hides it,
     * regular screens show it.
     */
    fun setBottomNavVisible(visible: Boolean) {
        binding.bottomNav.visibility = if (visible) View.VISIBLE else View.GONE
    }
}