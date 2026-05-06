package com.vshum.turbogum

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import com.vshum.turbogum.databinding.ActivityMainBinding
import com.vshum.turbogum.databinding.ViewProNavBarBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appNavigator: AppNavigator
    private lateinit var navBinding: ViewProNavBarBinding
    private lateinit var texts: List<TextView>

    private lateinit var items: List<View>
    private lateinit var icons: List<ImageView>

    private val colors by lazy {
        listOf(
            getColor(R.color.series_3_bg),
            getColor(R.color.series_5_bg),
            getColor(R.color.series_2_bg),
            getColor(R.color.series_1_bg)
        )
    }

    private var currentIndex = 0

    private var colorAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appNavigator =
            (applicationContext as App).servicesLocator.providerNavigator(this)

        setupProNav()
        setupBackPress()

        if (savedInstanceState == null) {
            appNavigator.navigateTo(Screen.SPLASH_SCREEN)
        }
    }

    private fun setupProNav() {
        navBinding = ViewProNavBarBinding.bind(binding.proNavBar.root)

        items = listOf(
            navBinding.navHome,
            navBinding.navFavorites,
            navBinding.navNotes,
            navBinding.navProfile
        )

        icons = listOf(
            navBinding.iconHome,
            navBinding.iconFavorites,
            navBinding.iconNotes,
            navBinding.iconProfile
        )

        items.forEachIndexed { index, view ->
            view.setOnClickListener {
                selectTab(index, animate = true)
                navigate(index)
            }
        }

        texts = listOf(
            navBinding.textHome,
            navBinding.textFavorites,
            navBinding.textNotes,
            navBinding.textProfile
        )

        navBinding.root.doOnLayout {
            selectTab(0, animate = false)
        }
    }

    private fun navigate(index: Int) {
        when (index) {
            0 -> appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
            1 -> appNavigator.navigateTo(Screen.FAVOURITE)
            2 -> appNavigator.navigateTo(Screen.NOTES_SCREEN)
            3 -> appNavigator.navigateTo(Screen.PROFILE_SCREEN)
        }
    }

    private fun selectTab(index: Int, animate: Boolean) {

        val target = items[index]
        val pill = navBinding.navPill

        val startColor = colors[currentIndex]
        val endColor = colors[index]

        currentIndex = index

        pill.post {

            val targetX = calculatePillX(target, pill)

            // 🚀 АНИМАЦИЯ ДВИЖЕНИЯ PILL
            if (animate) {
                pill.animate()
                    .x(targetX)
                    .setDuration(250)
                    .start()
            } else {
                pill.x = targetX
            }

            // 🎨 ПЛАВНАЯ АНИМАЦИЯ ЦВЕТА
            animatePillColor(pill, startColor, endColor)
        }

        // 🎯 ИКОНКИ
        icons.forEachIndexed { i, icon ->
            if (i == index) {
                icon.setColorFilter(getColor(R.color.white))
                icon.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(200)
                    .start()
            } else {
                icon.setColorFilter(getColor(R.color.text_3))
                icon.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }
        }

        texts.forEachIndexed { i, text ->

            if (i == index) {

                // 🎯 активный текст = как иконка (белый)
                text.setTextColor(getColor(R.color.white))

                text.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(200)
                    .start()

            } else {

                // 🎯 неактивный текст = как раньше (адаптивный под тему)
                text.setTextColor(getColor(R.color.text_3))

                text.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }
        }
    }

    // 🔥 ПЛАВНАЯ АНИМАЦИЯ ЦВЕТА
    private fun animatePillColor(view: View, startColor: Int, endColor: Int) {

        colorAnimator?.cancel()

        val drawable = view.background as GradientDrawable

        colorAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            startColor,
            endColor
        ).apply {
            duration = 250

            addUpdateListener { animator ->
                val color = animator.animatedValue as Int
                drawable.setColor(color)
            }

            start()
        }
    }

    private fun calculatePillX(target: View, pill: View): Float {
        val centerX = target.left + target.width / 2f
        val pillHalf = pill.width / 2f
        return centerX - pillHalf
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        supportFragmentManager.popBackStack()
                        return
                    }
                    showExitDialog()
                }
            }
        )
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Выход")
            .setMessage("Хотите выйти из приложения?")
            .setPositiveButton("Выйти") { _, _ -> finish() }
            .setNegativeButton("Отмена", null)
            .show()
    }

    fun setBottomNavVisible(visible: Boolean) {
        binding.proNavBar.root.visibility =
            if (visible) View.VISIBLE else View.GONE
    }
}