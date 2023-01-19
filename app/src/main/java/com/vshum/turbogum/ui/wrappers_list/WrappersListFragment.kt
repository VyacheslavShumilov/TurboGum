package com.vshum.turbogum.ui.wrappers_list

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vshum.turbogum.App
import com.vshum.turbogum.databinding.FragmentWrappersListBinding
import com.vshum.turbogum.navigator.AppNavigatorParamWrapper
import com.vshum.turbogum.navigator.ScreenParamWrapper


class WrappersListFragment : Fragment() {

    private lateinit var binding: FragmentWrappersListBinding
    private lateinit var appNavigatorParamWrapper: AppNavigatorParamWrapper




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWrappersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigatorParamWrapper =
            (context.applicationContext as App).servicesLocator.providerNavigatorParamWrapper(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            imageBtnTurbo.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.TURBO, "Turbo")
            }
            imageBtnTurbo2000.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.TURBO, "Turbo 2000")
            }
            imageBtnTurboClassic.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.TURBO, "Turbo classic")
            }
            imageBtnTurboLegends.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.TURBO, "Turbo legends")
            }
            imageBtnTurboPower.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.TURBO, "Turbo power")
            }
            imageBtnTurboSport.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.TURBO, "Turbo sport")
            }
            imageBtnTurboSport2003.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.TURBO, "Turbo sport 2003")
            }
            imageBtnTurboSuper2003.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.TURBO, "Turbo super 2003")
            }
            imageBtnTurboSuper2007.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.TURBO, "Turbo super 2007")
            }
        }
    }
}