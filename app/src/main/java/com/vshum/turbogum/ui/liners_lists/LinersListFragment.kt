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
    private lateinit var linersListViewModel: LinersListViewModel

    private val linersListLocal: ArrayList<Liner> by lazy { linersListViewModel.linersListLocal }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLinersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linersListViewModel =
            ViewModelProvider(requireActivity()).get(LinersListViewModel::class.java)

        if (linersListViewModel.linersSeries1List.isEmpty() ||
            linersListViewModel.linersSeries2List.isEmpty() ||
            linersListViewModel.linersSeries3List.isEmpty() ||
            linersListViewModel.linersSeries4List.isEmpty() ||
            linersListViewModel.linersSeries5List.isEmpty() ||
            linersListViewModel.linersSuper1List.isEmpty() ||
            linersListViewModel.linersSuper2List.isEmpty() ||
            linersListViewModel.linersSuper3List.isEmpty() ||
            linersListViewModel.linersSport1List.isEmpty() ||
            linersListViewModel.linersSport2List.isEmpty() ||
            linersListViewModel.linersClassic1List.isEmpty() ||
            linersListViewModel.linersClassic2List.isEmpty()
        ) {
            presenter = LinersListPresenterImpl()
            presenter.attachView(this)
            presenter.responseData()
        } else {
            when (series) {
                "Серия 1" -> initRecyclerView(linersListViewModel.linersSeries1List)
                "Серия 2" -> initRecyclerView(linersListViewModel.linersSeries2List)
                "Серия 3" -> initRecyclerView(linersListViewModel.linersSeries3List)
                "Серия 4" -> initRecyclerView(linersListViewModel.linersSeries4List)
                "Серия 5" -> initRecyclerView(linersListViewModel.linersSeries5List)
                "Super 1" -> initRecyclerView(linersListViewModel.linersSuper1List)
                "Super 2" -> initRecyclerView(linersListViewModel.linersSuper2List)
                "Super 3" -> initRecyclerView(linersListViewModel.linersSuper3List)
                "Sport 1" -> initRecyclerView(linersListViewModel.linersSport1List)
                "Sport 2" -> initRecyclerView(linersListViewModel.linersSport2List)
                "Classic 1" -> initRecyclerView(linersListViewModel.linersClassic1List)
                "Classic 2" -> initRecyclerView(linersListViewModel.linersClassic2List)
            }
        }
    }

    override fun onSuccessList(linersList: ArrayList<Liner>) {
        // Update cached data
        linersListLocal.clear()
        linersListLocal.addAll(linersList)

        when (series) {
            "Серия 1" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersSeries1List.clear()
                linersListViewModel.linersSeries1List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersSeries1List)
            }
            "Серия 2" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersSeries2List.clear()
                linersListViewModel.linersSeries2List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersSeries2List)
            }
            "Серия 3" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersSeries3List.clear()
                linersListViewModel.linersSeries3List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersSeries3List)

            }
            "Серия 4" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersSeries4List.clear()
                linersListViewModel.linersSeries4List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersSeries4List)

            }
            "Серия 5" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersSeries5List.clear()
                linersListViewModel.linersSeries5List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersSeries5List)
            }
            "Super 1" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersSuper1List.clear()
                linersListViewModel.linersSuper1List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersSuper1List)
            }
            "Super 2" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersSuper2List.clear()
                linersListViewModel.linersSuper2List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersSuper2List)
            }
            "Super 3" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersSuper3List.clear()
                linersListViewModel.linersSuper3List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersSuper3List)
            }
            "Sport 1" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersSport1List.clear()
                linersListViewModel.linersSport1List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersSport1List)
            }
            "Sport 2" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersSport2List.clear()
                linersListViewModel.linersSport2List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersSport2List)
            }
            "Classic 1" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersClassic1List.clear()
                linersListViewModel.linersClassic1List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersClassic1List)
            }
            "Classic 2" -> {
                val filteredItems = linersListLocal.filter { it.series == series }
                linersListViewModel.linersClassic2List.clear()
                linersListViewModel.linersClassic2List.addAll(filteredItems)
                initRecyclerView(linersListViewModel.linersClassic2List)
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

