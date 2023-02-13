package com.vshum.turbogum.ui.liners_lists

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    lateinit var linersSeries1List: ArrayList<Liner>
    lateinit var linersSeries2List: ArrayList<Liner>
    lateinit var linersSeries3List: ArrayList<Liner>
    lateinit var linersSeries4List: ArrayList<Liner>
    lateinit var linersSeries5List: ArrayList<Liner>
    lateinit var linersSuper1List: ArrayList<Liner>
    lateinit var linersSuper2List: ArrayList<Liner>
    lateinit var linersSuper3List: ArrayList<Liner>
    lateinit var linersSport1List: ArrayList<Liner>
    lateinit var linersSport2List: ArrayList<Liner>
    lateinit var linersSport3List: ArrayList<Liner>
    lateinit var linersSport4List: ArrayList<Liner>
    lateinit var linersSport5List: ArrayList<Liner>
    lateinit var linersClassic1List: ArrayList<Liner>
    lateinit var linersClassic2List: ArrayList<Liner>
    lateinit var linersPowerList: ArrayList<Liner>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLinersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (series == null) {
            series = ""
        }

        linersSeries1List = ArrayList()
        linersSeries2List = ArrayList()
        linersSeries3List = ArrayList()
        linersSeries4List = ArrayList()
        linersSeries5List = ArrayList()
        linersSuper1List = ArrayList()
        linersSuper2List = ArrayList()
        linersSuper3List = ArrayList()
        linersSport1List = ArrayList()
        linersSport2List = ArrayList()
        linersSport3List = ArrayList()
        linersSport4List = ArrayList()
        linersSport5List = ArrayList()
        linersClassic1List = ArrayList()
        linersClassic2List = ArrayList()
        linersPowerList = ArrayList()

        presenter = LinersListPresenterImpl()
        presenter.attachView(this)
        presenter.responseData()
    }

    override fun onSuccessList(linersList: ArrayList<Liner>) {
        for (i in linersList) {
            if(i.series != null) {
                when (i.series) {
                    "Серия 1" -> linersSeries1List.add(i)
                    "Серия 2" -> linersSeries2List.add(i)
                    "Серия 3" -> linersSeries3List.add(i)
                    "Серия 4" -> linersSeries4List.add(i)
                    "Серия 5" -> linersSeries5List.add(i)
                    "Super 1" -> linersSuper1List.add(i)
                    "Super 2" -> linersSuper2List.add(i)
                    "Super 3" -> linersSuper3List.add(i)
                    "Sport 1" -> linersSport1List.add(i)
                    "Sport 2"-> linersSport2List.add(i)
                    "Sport 3"-> linersSport3List.add(i)
                    "Sport 4"-> linersSport4List.add(i)
                    "Sport 5"-> linersSport5List.add(i)
                    "Classic 1"-> linersClassic1List.add(i)
                    "Classic 2"-> linersClassic2List.add(i)
                    "Power"-> linersPowerList.add(i)
                }
            }
        }

        linersList.clear()

        when (series) {
            "Серия 1" -> {
                linersList.addAll(linersSeries1List)
                initRecyclerView(linersList)
            }
            "Серия 2" -> {
                linersList.addAll(linersSeries2List)
                initRecyclerView(linersList)
            }
            "Серия 3" -> {
                linersList.addAll(linersSeries3List)
                initRecyclerView(linersList)
            }
            "Серия 4" -> {
                linersList.addAll(linersSeries4List)
                initRecyclerView(linersList)
            }
            "Серия 5" -> {
                linersList.addAll(linersSeries5List)
                initRecyclerView(linersList)
            }
            "Super 1" -> {
                linersList.addAll(linersSuper1List)
                initRecyclerView(linersList)
            }
            "Super 2" -> {
                linersList.addAll(linersSuper2List)
                initRecyclerView(linersList)
            }
            "Super 3" -> {
                linersList.addAll(linersSuper3List)
                initRecyclerView(linersList)
            }
            "Sport 1" -> {
                linersList.addAll(linersSport1List)
                initRecyclerView(linersList)
            }
            "Sport 2" -> {
                linersList.addAll(linersSport2List)
                initRecyclerView(linersList)
            }
            "Sport 3" -> {
                linersList.addAll(linersSport3List)
                initRecyclerView(linersList)
            }
            "Sport 4" -> {
                linersList.addAll(linersSport4List)
                initRecyclerView(linersList)
            }
            "Sport 5" -> {
                linersList.addAll(linersSport5List)
                initRecyclerView(linersList)
            }
            "Classic 1" -> {
                linersList.addAll(linersClassic1List)
                initRecyclerView(linersList)
            }
            "Classic 2" -> {
                linersList.addAll(linersClassic2List)
                initRecyclerView(linersList)
            }
            "Power" -> {
                linersList.addAll(linersPowerList)
                initRecyclerView(linersList)
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

