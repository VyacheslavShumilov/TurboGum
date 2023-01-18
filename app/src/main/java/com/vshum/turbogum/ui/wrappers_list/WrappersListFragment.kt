package com.vshum.turbogum.ui.wrappers_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vshum.turbogum.databinding.FragmentWrappersListBinding
import com.vshum.turbogum.model.WrappersList
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.ui.wrappers_list.adapter.AdapterWrappersList
import com.vshum.turbogum.ui.wrappers_list.impl.WrappersListContract
import com.vshum.turbogum.ui.wrappers_list.impl.WrappersListPresenterImpl


class WrappersListFragment : Fragment() {

    private lateinit var binding: FragmentWrappersListBinding
    private lateinit var presenter: WrappersListPresenterImpl
    private lateinit var appNavigator: AppNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWrappersListBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        presenter = WrappersListPresenterImpl()
//        presenter.attachView(this)
//        presenter.responseData()
//    }
//
//    override fun onClickLiner(nameWrapper: String) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onSuccessList(wrappersList: ArrayList<WrappersList>) {
//        val adapterWrappersList = AdapterWrappersList(wrappersList, this)
//        binding.recyclerView.adapter = adapterWrappersList
//    }
//
//    override fun error(errMessage: String) {
//        binding.noInternetLayout.visibility = View.VISIBLE
//        binding.btnRepeat.setOnClickListener {
//            presenter.responseData()
//        }
//    }
//
//    override fun progress(show: Boolean) {
//        if (show) {
//            binding.noInternetLayout.visibility = View.GONE
//            binding.progressBar.visibility = View.VISIBLE
//        } else {
//            binding.noInternetLayout.visibility = View.GONE
//            binding.progressBar.visibility = View.GONE
//        }
//    }
}