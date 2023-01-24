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

class LinersListFragment(var nameWrapper: String) : Fragment(), LinersListContract.View,
    AdapterLinersList.SetOnClickListener {

    private lateinit var binding: FragmentLinersListBinding
    private lateinit var presenter: LinersListPresenterImpl
    private lateinit var appNavigatorParamLiner: AppNavigatorParamLiner
    lateinit var linersTurboList: ArrayList<Liner>
    lateinit var linersTurbo2000List: ArrayList<Liner>
    lateinit var linersTurboClassicList: ArrayList<Liner>
    lateinit var linersTurboLegendsList: ArrayList<Liner>
    lateinit var linersTurboPowerList: ArrayList<Liner>
    lateinit var linersTurboSportList: ArrayList<Liner>
    lateinit var linersTurboSport2003List: ArrayList<Liner>
    lateinit var linersTurboSuper2003List: ArrayList<Liner>
    lateinit var linersTurboSuper2007List: ArrayList<Liner>

//    private val linersLists = hashMapOf(
//        "Turbo" to ArrayList<Liner>(),
//        "Turbo 2000" to ArrayList<Liner>(),
//        "Turbo classic" to ArrayList<Liner>(),
//        "Turbo legends" to ArrayList<Liner>(),
//        "Turbo power" to ArrayList<Liner>(),
//        "Turbo sport" to ArrayList<Liner>(),
//        "Turbo sport 2003" to ArrayList<Liner>(),
//        "Turbo super 2003" to ArrayList<Liner>(),
//        "Turbo super 2007" to ArrayList<Liner>()
//    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLinersListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (nameWrapper == null) {
            nameWrapper = ""
        }

        linersTurboList = ArrayList()
        linersTurbo2000List = ArrayList()
        linersTurboClassicList = ArrayList()
        linersTurboLegendsList = ArrayList()
        linersTurboPowerList = ArrayList()
        linersTurboSportList = ArrayList()
        linersTurboSport2003List = ArrayList()
        linersTurboSuper2003List = ArrayList()
        linersTurboSuper2007List = ArrayList()


        presenter = LinersListPresenterImpl()
        presenter.attachView(this)
        presenter.responseData()
    }


//    override fun onSuccessList(linersList: ArrayList<Liner>) {
//        for (i in linersList) {
//            val list = linersLists[i.nameWrapper]
//            if (list != null) list.add(i)
//        }
//
//        val targetList = linersLists[nameWrapper]
//        if (targetList != null) {
//            val filteredList = ArrayList<Liner>()
//            filteredList.addAll(targetList)
//            val adapterLiners = AdapterLinersList(filteredList, this)
//            binding.recyclerView.adapter = adapterLiners
//            setRecyclerViewAutoFit(binding.recyclerView)
//        }
//    }


    override fun onSuccessList(linersList: ArrayList<Liner>) {
        for (i in linersList) {
            if(i.nameWrapper != null) {
                when (i.nameWrapper) {
                    "Turbo" -> linersTurboList.add(i)
                    "Turbo 2000" -> linersTurbo2000List.add(i)
                    "Turbo classic" -> linersTurboClassicList.add(i)
                    "Turbo legends" -> linersTurboLegendsList.add(i)
                    "Turbo power" -> linersTurboPowerList.add(i)
                    "Turbo sport" -> linersTurboSportList.add(i)
                    "Turbo sport 2003" -> linersTurboSport2003List.add(i)
                    "Turbo super 2003" -> linersTurboSuper2003List.add(i)
                    "Turbo super 2007" -> linersTurboSuper2007List.add(i)
                }
            }
        }

        linersList.clear()

        when (nameWrapper) {
            "Turbo" -> {
                linersList.addAll(linersTurboList)
                val adapterLiners = AdapterLinersList(linersList, this)
                binding.recyclerView.adapter = adapterLiners
                setRecyclerViewAutoFit(binding.recyclerView)
            }
            "Turbo 2000" -> {
                linersList.addAll(linersTurbo2000List)
                val adapterLiners = AdapterLinersList(linersList, this)
                binding.recyclerView.adapter = adapterLiners
                setRecyclerViewAutoFit(binding.recyclerView)


            }
            "Turbo classic" -> {
                linersList.addAll(linersTurboClassicList)
                val adapterLiners = AdapterLinersList(linersList, this)
                binding.recyclerView.adapter = adapterLiners
                setRecyclerViewAutoFit(binding.recyclerView)

            }
            "Turbo legends" -> {
                linersList.addAll(linersTurboLegendsList)
                val adapterLiners = AdapterLinersList(linersList, this)
                binding.recyclerView.adapter = adapterLiners
                setRecyclerViewAutoFit(binding.recyclerView)

            }
            "Turbo power" -> {
                linersList.addAll(linersTurboPowerList)
                val adapterLiners = AdapterLinersList(linersList, this)
                binding.recyclerView.adapter = adapterLiners
                setRecyclerViewAutoFit(binding.recyclerView)

            }
            "Turbo sport" -> {
                linersList.addAll(linersTurboSportList)
                val adapterLiners = AdapterLinersList(linersList, this)
                binding.recyclerView.adapter = adapterLiners
                setRecyclerViewAutoFit(binding.recyclerView)

            }
            "Turbo sport 2003" -> {
                linersList.addAll(linersTurboSport2003List)
                val adapterLiners = AdapterLinersList(linersList, this)
                binding.recyclerView.adapter = adapterLiners
                setRecyclerViewAutoFit(binding.recyclerView)

            }
            "Turbo super 2003" -> {
                linersList.addAll(linersTurboSuper2003List)
                val adapterLiners = AdapterLinersList(linersList, this)
                binding.recyclerView.adapter = adapterLiners
                setRecyclerViewAutoFit(binding.recyclerView)

            }
            "Turbo super 2007" -> {
                linersList.addAll(linersTurboSuper2007List)
                val adapterLiners = AdapterLinersList(linersList, this)
                binding.recyclerView.adapter = adapterLiners
                setRecyclerViewAutoFit(binding.recyclerView)
            }
        }
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

