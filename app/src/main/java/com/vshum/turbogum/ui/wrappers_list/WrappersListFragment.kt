package com.vshum.turbogum.ui.wrappers_list

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vshum.turbogum.App
import com.vshum.turbogum.databinding.FragmentWrappersListBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen


class WrappersListFragment : Fragment() {

    private lateinit var binding: FragmentWrappersListBinding
    private lateinit var appNavigator: AppNavigator


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWrappersListBinding.inflate(inflater, container, false)
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

            imageBtnTurbo.setOnClickListener {
                appNavigator.navigateTo(Screen.TURBO)
            }
            imageBtnTurbo2000.setOnClickListener {
                appNavigator.navigateTo(Screen.TURBO_2000)
            }
            imageBtnTurboClassic.setOnClickListener {
                appNavigator.navigateTo(Screen.TURBO_CLASSIC)
            }
            imageBtnTurboLegends.setOnClickListener {
                appNavigator.navigateTo(Screen.TURBO_LEGENDS)
            }
            imageBtnTurboPower.setOnClickListener {
                appNavigator.navigateTo(Screen.TURBO_POWER)
            }
            imageBtnTurboSport.setOnClickListener {
                appNavigator.navigateTo(Screen.TURBO_SPORT)
            }
            imageBtnTurboSport2003.setOnClickListener {
                appNavigator.navigateTo(Screen.TURBO_SPORT_2003)
            }
            imageBtnTurboSuper2003.setOnClickListener {
                appNavigator.navigateTo(Screen.TURBO_SUPER_2003)
            }
            imageBtnTurboSuper2007.setOnClickListener {
                appNavigator.navigateTo(Screen.TURBO_SUPER_2007)
            }
        }

    }


}