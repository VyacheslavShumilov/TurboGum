package com.vshum.turbogum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vshum.turbogum.databinding.ActivityMainBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen
import com.vshum.turbogum.ui.scan.ScanFragment
import com.vshum.turbogum.ui.community.CommunityFragment
import com.vshum.turbogum.ui.profile.ProfileFragment
import com.vshum.turbogum.ui.wrappers_list.WrappersListFragment
import com.vshum.turbogum.ui.favourite_list.FavouriteListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appNavigator: AppNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appNavigator = (applicationContext as App).servicesLocator.providerNavigator(this)

        if (savedInstanceState == null) {
            appNavigator.navigateTo(Screen.START_SCREEN)
        }

        setupBottomNav()
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
                    true
                }
                R.id.nav_collection -> {
                    appNavigator.navigateTo(Screen.FAVOURITE)
                    true
                }
//                R.id.nav_scan -> {
//                    appNavigator.navigateTo(Screen.SCAN_SCREEN)
//                    true
//                }
//                R.id.nav_community -> {
//                    appNavigator.navigateTo(Screen.COMMUNITY_SCREEN)
//                    true
//                }
                R.id.nav_profile -> {
                    appNavigator.navigateTo(Screen.PROFILE_SCREEN)
                    true
                }
                R.id.nav_favorites -> {
                    appNavigator.navigateTo(Screen.FAVOURITE)
                    true
                }
                else -> false
            }
        }
    }

    fun setActiveNavItem(itemId: Int) {
        binding.bottomNav.selectedItemId = itemId
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}