package com.vshum.turbogum.ui.liners_lists.turbo

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vshum.turbogum.App
import com.vshum.turbogum.databinding.FragmentTurboBinding
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.navigator.AppNavigatorParam
import com.vshum.turbogum.navigator.ScreenParam
import com.vshum.turbogum.ui.liners_lists.turbo.adapter.AdapterTurbo
import com.vshum.turbogum.ui.liners_lists.turbo.impl.TurboContract
import com.vshum.turbogum.ui.liners_lists.turbo.impl.TurboPresenterImpl


class TurboFragment : Fragment(),  TurboContract.View, AdapterTurbo.SetOnClickListener{

    private lateinit var binding: FragmentTurboBinding
    private lateinit var presenter: TurboPresenterImpl
    private lateinit var appNavigatorParam: AppNavigatorParam

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTurboBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = TurboPresenterImpl()
        presenter.attachView(this)
        presenter.responseData()
    }


    override fun onSuccessList(linersTurboList: ArrayList<Liner>) {
        val adapterLiners = AdapterTurbo(linersTurboList, this)
        binding.recyclerView.adapter = adapterLiners
    }

    override fun error(errMessage: String) {
        binding.noInternetLayout.visibility = View.VISIBLE
        binding.btnRepeat.setOnClickListener {
            presenter.responseData()
        }
    }

    override fun progress(show: Boolean) {
        if (show) {
            binding.noInternetLayout.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.btnRepeat.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onClickLiner(idLiner: String) {
        appNavigatorParam.navigateToParam(ScreenParam.TURBO_LINER, idLiner)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigatorParam = (context.applicationContext as App).servicesLocator.providerNavigatorParam(requireActivity())
    }

}