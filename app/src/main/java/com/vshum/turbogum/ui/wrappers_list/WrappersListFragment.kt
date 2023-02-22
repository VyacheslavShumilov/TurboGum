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

            imageBtnSeries1.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Серия 1")

            }
            imageBtnSeries2.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Серия 2")
            }
            imageBtnSeries3.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Серия 3")
            }
            imageBtnSeries4.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Серия 4")
            }
            imageBtnSeries5.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Серия 5")
            }
            imageBtnSuper1.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Super 1")
            }
            imageBtnSuper2.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Super 2")
            }
            imageBtnSuper3.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Super 3")
            }
            imageBtnSport1.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Sport 1")
            }
            imageBtnSport2.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Sport 2")
            }
//            imageBtnSport3.setOnClickListener {
//                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Sport 3")
//            }
//            imageBtnSport4.setOnClickListener {
//                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Sport 4")
//            }
//            imageBtnSport5.setOnClickListener {
//                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Sport 5")
//            }
            imageBtnClassic1.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Classic 1")
            }
            imageBtnClassic2.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Classic 2")
            }
//            imageBtnPower.setOnClickListener {
//                appNavigatorParamWrapper.navigateToParamWrapper(ScreenParamWrapper.SERIES_1, "Power")
//            }
        }
    }
}