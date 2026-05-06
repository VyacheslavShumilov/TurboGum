package com.vshum.turbogum.ui.liners_lists

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.FragmentLinersListBinding
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorParamLiners
import com.vshum.turbogum.navigator.Screen
import com.vshum.turbogum.ui.liners_lists.adapter.AdapterLinersList
import com.vshum.turbogum.ui.liners_lists.contract.LinersListContract
import com.vshum.turbogum.ui.liners_lists.contract.LinersListPresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LinersListFragment(private val seriesKey: String) :
    Fragment(),
    LinersListContract.View {

    private var _binding: FragmentLinersListBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private lateinit var appNavigatorParamLiners: AppNavigatorParamLiners
    private lateinit var appDao: LinersDao
    private lateinit var presenter: LinersListContract.Presenter

    private lateinit var layoutManager: GridLayoutManager
    private var adapter: AdapterLinersList? = null

    private val fullList = ArrayList<Liner>()

    private enum class Filter { ALL, OWNED, MISSING }
    private var activeFilter = Filter.ALL
    private var query: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupTitle()
        setupRecycler()
        setupSearch()
        setupChips()

        presenter = LinersListPresenterImpl(this, requireContext())
        presenter.startScreen(seriesKey)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val app = context.applicationContext as App
        appNavigator = app.servicesLocator.providerNavigator(requireActivity())
        appNavigatorParamLiners =
            app.servicesLocator.providerNavigatorParamLiners(requireActivity())
        appDao = app.getDatabase().linersDao()
    }

    private fun setupHeader() {
        val header = binding.headerInclude.root
        header.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
        }
    }

    private fun setupTitle() {
        binding.seriesTitle.text = seriesNameFor(seriesKey)
        binding.seriesSubtitle.text = seriesSubtitleFor(seriesKey)
    }

    private fun setupRecycler() {
        layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.layoutManager = layoutManager
    }

    private fun rebuildAdapter(filtered: List<Liner>) {
        adapter = AdapterLinersList(
            fullList = filtered,
            listener = object : AdapterLinersList.SetOnClickListener {
                override fun onClickLiner(liner: Liner) {
                    appNavigatorParamLiners.navigateToParamLiner(Screen.LINER_SCREEN, liner)
                }
            },
            appDao = appDao
        )
        binding.recyclerView.adapter = adapter
    }

    // ── Search ──
    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                query = s?.toString()?.trim()?.lowercase() ?: ""
                applyFilter()
            }
        })
    }

    // ── Filter ──
    private fun setupChips() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, ids ->
            activeFilter = when (ids.firstOrNull()) {
                R.id.chipOwned -> Filter.OWNED
                R.id.chipMissing -> Filter.MISSING
                else -> Filter.ALL
            }
            applyFilter()
        }
    }

    private fun applyFilter() {
        lifecycleScope.launch(Dispatchers.IO) {

            val ownedSet = try {
                appDao.getAllFavouriteLiners()
                    .map { it.uniqueNumber }
                    .toHashSet()
            } catch (e: Exception) {
                hashSetOf()
            }

            val filtered = fullList.filter { liner ->

                val matchesQuery = query.isEmpty() ||
                        liner.brand.lowercase().contains(query) ||
                        liner.model.lowercase().contains(query) ||
                        liner.numberLiner.contains(query)

                val isOwned = ownedSet.contains(liner.uniqueNumber)

                val matchesFilter = when (activeFilter) {
                    Filter.ALL -> true
                    Filter.OWNED -> isOwned
                    Filter.MISSING -> !isOwned
                }

                matchesQuery && matchesFilter
            }

            withContext(Dispatchers.Main) {
                if (_binding == null) return@withContext
                rebuildAdapter(filtered)
                updateProgressCard(ownedSet)
            }
        }
    }

    private fun updateProgressCard(ownedSet: HashSet<String>) {
        val total = fullList.size
        val owned = fullList.count { ownedSet.contains(it.uniqueNumber) }
        val percent = if (total > 0) owned * 100 / total else 0

        val card = binding.progressCard.root
        card.findViewById<TextView>(R.id.progressCount)?.text = "$owned/$total"
        card.findViewById<android.widget.ProgressBar>(R.id.progressBar)?.progress = percent
    }

    override fun onSuccessList(list: List<Liner>) {
        fullList.clear()
        fullList.addAll(list)
        applyFilter()
    }

    // ── helpers ──
    private fun seriesNameFor(key: String) = key
    private fun seriesSubtitleFor(key: String) = ""
}