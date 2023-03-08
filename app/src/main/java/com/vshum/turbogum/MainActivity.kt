package com.vshum.turbogum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.vshum.turbogum.databinding.ActivityMainBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen
import com.vshum.turbogum.ui.liners_lists.LinersListFragment

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
    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        if (supportFragmentManager.backStackEntryCount == 0) {
//            finish()
//        }
//    }
}