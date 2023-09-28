package com.vshum.turbogum.ui

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.vshum.turbogum.App
import com.vshum.turbogum.Constants
import com.vshum.turbogum.databinding.FragmentStartScreenBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen

class StartScreenFragment : Fragment() {
    private lateinit var binding: FragmentStartScreenBinding
    private lateinit var appNavigator: AppNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = context?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val password = sharedPreferences?.getString("password", "")

//        val animation = TranslateAnimation(0f, 0f, 0f, 50f.dpToPx())
//        animation.duration = 500 // длительность анимации в миллисекундах
//        animation.repeatCount = Animation.INFINITE // бесконечное повторение
//        animation.repeatMode = Animation.REVERSE // обратное повторение


        with(binding) {
            logoImage.setOnClickListener {
                if (password.isNullOrBlank() || password != Constants.PASSWORD) {
                    //appNavigator.navigateTo(Screen.REGISTRATION_SCREEN) //убрал регистрацию в приложении
                    appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
                } else {
                    appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
                }
            }
//            logoContainer.startAnimation(animation)

//            Handler(Looper.getMainLooper()).postDelayed({
//                animation.cancel()
//            }, 1000)

            developersBtn.setOnClickListener {
                appNavigator.navigateTo(Screen.DEVELOPERS_SCREEN)
            }

        }
    }

    private fun Float.dpToPx(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
        )
    }
}