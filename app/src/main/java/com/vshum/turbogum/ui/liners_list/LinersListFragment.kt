package com.vshum.turbogum.ui.liners_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentLinersListBinding
import com.vshum.turbogum.model.LinersList
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.ui.liners_list.adapter.AdapterLinersList
import com.vshum.turbogum.ui.liners_list.impl.LinersListContract
import com.vshum.turbogum.ui.liners_list.impl.LinersListPresenterImpl


class LinersListFragment : Fragment(), LinersListContract.View,
    AdapterLinersList.SetOnClickListener {

    private lateinit var binding: FragmentLinersListBinding
    private lateinit var presenter: LinersListPresenterImpl
    private lateinit var appNavigator: AppNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLinersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = LinersListPresenterImpl()
        presenter.attachView(this)
        presenter.responseData()
    }

    override fun onClickLiner(nameLiner: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccessList(linersList: ArrayList<LinersList>) {
        val adapterLinersList = AdapterLinersList(linersList, this)
        binding.recyclerView.adapter = adapterLinersList
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
            binding.noInternetLayout.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }
    }
}