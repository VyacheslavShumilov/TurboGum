package com.vshum.turbogum

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vshum.turbogum.databinding.ActivityMainBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen

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
            appNavigator.navigateTo(Screen.SPLASH_SCREEN)
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home      -> { appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN); true }
                R.id.nav_favorites -> { appNavigator.navigateTo(Screen.FAVOURITE);            true }
                R.id.nav_notes     -> { appNavigator.navigateTo(Screen.NOTES_SCREEN);         true }
                R.id.nav_profile   -> { appNavigator.navigateTo(Screen.PROFILE_SCREEN);       true }
                else -> false
            }
        }
    }

    fun setBottomNavVisible(visible: Boolean) {
        binding.bottomNav.visibility = if (visible) View.VISIBLE else View.GONE
    }
}