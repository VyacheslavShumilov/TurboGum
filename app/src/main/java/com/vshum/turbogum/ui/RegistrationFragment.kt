package com.vshum.turbogum.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.transition.Slide
import com.vshum.turbogum.App
import com.vshum.turbogum.Constants
import com.vshum.turbogum.databinding.FragmentRegistrationBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen


class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var appNavigator: AppNavigator

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences(
            Constants.PASSWORD,
            Context.MODE_PRIVATE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val enterSlide = Slide()
        enterSlide.slideEdge = Gravity.END
        enterSlide.duration = 300
        enterSlide.interpolator = DecelerateInterpolator()
        enterTransition = enterSlide

        val exitSlide = Slide()
        exitSlide.slideEdge = Gravity.START
        exitSlide.duration = 300
        exitSlide.interpolator = DecelerateInterpolator()
        exitTransition = exitSlide
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            val savedPassword = sharedPreferences.getString(Constants.PASSWORD, "")
            if (savedPassword != null) {
                if (savedPassword.isNotEmpty()) {
                    appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
                    return
                }
            }


            loginButton.setOnClickListener {
                val password = passwordInput.text.toString()
                if (password == Constants.PASSWORD) {
                    sharedPreferences.edit().putString(Constants.PASSWORD, password).apply()
                    appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
                } else {
                    Toast.makeText(context, "Неправильный пароль", Toast.LENGTH_SHORT).show()
                }
            }

            helpButton.setOnClickListener {
                appNavigator.navigateTo(Screen.HELP_SCREEN)
            }
        }
    }
}