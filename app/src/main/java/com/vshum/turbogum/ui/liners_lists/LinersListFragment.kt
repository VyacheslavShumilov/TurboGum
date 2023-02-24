package com.vshum.turbogum.ui.liners_lists

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vshum.turbogum.App
import com.vshum.turbogum.databinding.FragmentLinersListBinding
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.navigator.AppNavigatorParamLiner
import com.vshum.turbogum.navigator.ScreenParamLiner
import com.vshum.turbogum.ui.liners_lists.adapter.AdapterLinersList
import com.vshum.turbogum.ui.liners_lists.impl.LinersListContract
import com.vshum.turbogum.ui.liners_lists.impl.LinersListPresenterImpl

class LinersListFragment(var series: String) : Fragment(), LinersListContract.View,
    AdapterLinersList.SetOnClickListener {

    private lateinit var binding: FragmentLinersListBinding
    private lateinit var presenter: LinersListPresenterImpl
    private lateinit var appNavigatorParamLiner: AppNavigatorParamLiner
    private lateinit var viewModel: LinersListViewModel

    private val linersListLocal: ArrayList<Liner> by lazy { viewModel.linersListLocal }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLinersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel =
            ViewModelProvider(requireActivity()).get(LinersListViewModel::class.java)

        if (viewModel.series1List.isEmpty() ||
            viewModel.series2List.isEmpty() ||
            viewModel.series3List.isEmpty() ||
            viewModel.series4List.isEmpty() ||
            viewModel.series5List.isEmpty() ||
            viewModel.super1List.isEmpty() ||
            viewModel.super2List.isEmpty() ||
            viewModel.super3List.isEmpty() ||
            viewModel.sport1List.isEmpty() ||
            viewModel.sport2List.isEmpty() ||
            viewModel.classic1List.isEmpty() ||
            viewModel.classic2List.isEmpty()
        ) {
            presenter = LinersListPresenterImpl()
            presenter.attachView(this)
            presenter.responseData()
        } else {
            when (series) {
                "Серия 1" -> initRecyclerView(viewModel.series1List)
                "Серия 2" -> initRecyclerView(viewModel.series2List)
                "Серия 3" -> initRecyclerView(viewModel.series3List)
                "Серия 4" -> initRecyclerView(viewModel.series4List)
                "Серия 5" -> initRecyclerView(viewModel.series5List)
                "Super 1" -> initRecyclerView(viewModel.super1List)
                "Super 2" -> initRecyclerView(viewModel.super2List)
                "Super 3" -> initRecyclerView(viewModel.super3List)
                "Sport 1" -> initRecyclerView(viewModel.sport1List)
                "Sport 2" -> initRecyclerView(viewModel.sport2List)
                "Classic 1" -> initRecyclerView(viewModel.classic1List)
                "Classic 2" -> initRecyclerView(viewModel.classic2List)
            }
        }
    }

    /***
     * Сортирока вкладышей по принципу:
     * - юзер кликнул на обертку, в этот фрагмент передается (series: String)
     * - получаю полный список из БД
     * - фильтрую полный список по series и добавляю отфильтрованный список в список ViewModel'и
     * - передаю отфильтрованный список в адаптер и отображаю на экране
     */

    override fun onSuccessList(linersList: ArrayList<Liner>) {
        // Update cached data
        linersListLocal.clear()
        linersListLocal.addAll(linersList)

        when (series) {
            "Серия 1" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.series1List.clear()
                viewModel.series1List.addAll(filteredItems)
                initRecyclerView(viewModel.series1List)
            }
            "Серия 2" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.series2List.clear()
                viewModel.series2List.addAll(filteredItems)
                initRecyclerView(viewModel.series2List)
            }
            "Серия 3" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.series3List.clear()
                viewModel.series3List.addAll(filteredItems)
                initRecyclerView(viewModel.series3List)

            }
            "Серия 4" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.series4List.clear()
                viewModel.series4List.addAll(filteredItems)
                initRecyclerView(viewModel.series4List)

            }
            "Серия 5" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.series5List.clear()
                viewModel.series5List.addAll(filteredItems)
                initRecyclerView(viewModel.series5List)
            }
            "Super 1" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.super1List.clear()
                viewModel.super1List.addAll(filteredItems)
                initRecyclerView(viewModel.super1List)
            }
            "Super 2" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.super2List.clear()
                viewModel.super2List.addAll(filteredItems)
                initRecyclerView(viewModel.super2List)
            }
            "Super 3" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.super3List.clear()
                viewModel.super3List.addAll(filteredItems)
                initRecyclerView(viewModel.super3List)
            }
            "Sport 1" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.sport1List.clear()
                viewModel.sport1List.addAll(filteredItems)
                initRecyclerView(viewModel.sport1List)
            }
            "Sport 2" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.sport2List.clear()
                viewModel.sport2List.addAll(filteredItems)
                initRecyclerView(viewModel.sport2List)
            }
            "Classic 1" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.classic1List.clear()
                viewModel.classic1List.addAll(filteredItems)
                initRecyclerView(viewModel.classic1List)
            }
            "Classic 2" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                viewModel.classic2List.clear()
                viewModel.classic2List.addAll(filteredItems)
                initRecyclerView(viewModel.classic2List)
            }
        }
    }

    private fun initRecyclerView(linersList: ArrayList<Liner>) {
        val adapterLiners = AdapterLinersList(linersList, this)
        binding.recyclerView.adapter = adapterLiners
        setRecyclerViewAutoFit(binding.recyclerView)
    }


    override fun error(errMessage: String) {
        binding.noInternetLayout.visibility = View.VISIBLE
        binding.btnRepeat.visibility = View.VISIBLE
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

    override fun onClickLiner(liner: Liner) {
        appNavigatorParamLiner.navigateToParamLiner(ScreenParamLiner.TURBO, liner)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigatorParamLiner =
            (context.applicationContext as App).servicesLocator.providerNavigatorParamLiners(
                requireActivity()
            )
    }

    //авто подсчет количества элементов по ширине в списке
    private fun setRecyclerViewAutoFit(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        layoutManager.spanCount = calculateNoOfColumns(recyclerView.context)
        recyclerView.layoutManager = layoutManager
    }

    private fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / 180 + 0.5).toInt()
    }
}

