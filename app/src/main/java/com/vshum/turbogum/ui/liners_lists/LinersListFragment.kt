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
import androidx.recyclerview.widget.RecyclerView
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

/**
 * SeriesList screen — sticker grid with pagination.
 *
 * Pagination: adapter shows first 20 items; RecyclerView scroll listener
 * triggers [AdapterLinersList.loadNextPage] when user is within 4 rows
 * of the bottom. Each page = 20 items = ~20 Picasso requests max.
 */
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

    // Full dataset loaded from network; filtered list passed to adapter
    private val fullList = ArrayList<Liner>()
    private var adapter: AdapterLinersList? = null

    private enum class Filter { ALL, OWNED, MISSING }
    private var activeFilter = Filter.ALL
    private var query: String = ""

    // ── Lifecycle ─────────────────────────────────────────────────────

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
        appNavigator            = app.servicesLocator.providerNavigator(requireActivity())
        appNavigatorParamLiners = app.servicesLocator.providerNavigatorParamLiners(requireActivity())
        appDao                  = app.getDatabase().linersDao()
    }

    // ── Header ────────────────────────────────────────────────────────

    private fun setupHeader() {
        val header = binding.headerInclude.root
        header.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
        }
        header.findViewById<TextView>(R.id.headerTitle)?.text = ""
    }

    private fun setupTitle() {
        binding.seriesTitle.text    = seriesNameFor(seriesKey)
        binding.seriesSubtitle.text = seriesSubtitleFor(seriesKey)
    }

    private fun seriesNameFor(key: String): String = when (key) {
        "series1"  -> "Серия 1"
        "series2"  -> "Серия 2"
        "series3"  -> "Серия 3"
        "series4"  -> "Серия 4"
        "series5"  -> "Серия 5"
        "super1"   -> "Super 1"
        "super2"   -> "Super 2"
        "super3"   -> "Super 3"
        "sport1"   -> "Sport 1"
        "sport2"   -> "Sport 2"
        "classic1" -> "Classic 1"
        "classic2" -> "Classic 2"
        else -> key
    }

    private fun seriesSubtitleFor(key: String): String = when (key) {
        "series1"  -> "№ 1–50 · 1989"
        "series2"  -> "№ 51–120 · 1990"
        "series3"  -> "№ 121–190 · 1991"
        "series4"  -> "№ 191–260 · 1992"
        "series5"  -> "№ 261–330 · 1993"
        "super1"   -> "№ 331–400 · 1993"
        "super2"   -> "№ 401–470 · 1994"
        "super3"   -> "№ 471–540 · 1995"
        "sport1"   -> "№ 1–70 · 1996"
        "sport2"   -> "№ 71–140 · 1997"
        "classic1" -> "№ 1–70 · 1998"
        "classic2" -> "№ 71–140 · 1999"
        else -> ""
    }

    // ── RecyclerView + pagination scroll listener ─────────────────────

    private fun setupRecycler() {
        layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.layoutManager = layoutManager

        // Scroll listener: load next page when within 4 rows of the bottom
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return   // only trigger on downward scroll
                val visibleItemCount    = layoutManager.childCount
                val totalItemCount      = layoutManager.itemCount
                val firstVisibleItem    = layoutManager.findFirstVisibleItemPosition()
                val threshold           = 2 * 2   // 2 rows × 2 columns
                if (visibleItemCount + firstVisibleItem + threshold >= totalItemCount) {
                    adapter?.loadNextPage()
                }
            }
        })
    }

    /**
     * Rebuild adapter with a fresh filtered list.
     * Called after data loads and after every filter/search change.
     */
    private fun rebuildAdapter(filtered: List<Liner>) {
        val newAdapter = AdapterLinersList(
            fullList = filtered,
            listener = object : AdapterLinersList.SetOnClickListener {
                override fun onClickLiner(liner: Liner) {
                    appNavigatorParamLiners.navigateToParamLiner(Screen.LINER_SCREEN, liner)
                }
            },
            appDao   = appDao,
            pageSize = 20
        )
        adapter = newAdapter
        binding.recyclerView.adapter = newAdapter
    }

    // ── Search ────────────────────────────────────────────────────────

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

    // ── Filter chips ──────────────────────────────────────────────────

    private fun setupChips() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, ids ->
            activeFilter = when (ids.firstOrNull()) {
                R.id.chipOwned   -> Filter.OWNED
                R.id.chipMissing -> Filter.MISSING
                else             -> Filter.ALL
            }
            applyFilter()
        }
    }

    // ── Filter logic ──────────────────────────────────────────────────

    private fun applyFilter() {
        lifecycleScope.launch(Dispatchers.IO) {
            val ownedSet = try {
                appDao.getAllFavouriteLiners().map { it.uniqueNumber }.toHashSet()
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
                    Filter.ALL     -> true
                    Filter.OWNED   -> isOwned
                    Filter.MISSING -> !isOwned
                }
                matchesQuery && matchesFilter
            }

            withContext(Dispatchers.Main) {
                if (_binding == null) return@withContext
                // Rebuild adapter with new filtered list — resets to page 1
                rebuildAdapter(filtered)
                updateProgressCard(ownedSet)
            }
        }
    }

    private fun updateProgressCard(ownedSet: HashSet<String>) {
        val total   = fullList.size
        val owned   = fullList.count { ownedSet.contains(it.uniqueNumber) }
        val percent = if (total > 0) (owned * 100 / total) else 0

        val card = binding.progressCard.root
        card.findViewById<TextView>(R.id.progressCount)?.text = "$owned/$total"
        card.findViewById<android.widget.ProgressBar>(R.id.progressBar)?.progress = percent
        card.findViewById<TextView>(R.id.legendOwned)?.text   = "Есть ($owned)"
        card.findViewById<TextView>(R.id.legendMissing)?.text = "Нет (${total - owned})"
    }

    // ── Contract.View ─────────────────────────────────────────────────

    override fun onSuccessList(list: List<Liner>) {
        fullList.clear()
        fullList.addAll(list)
        applyFilter()
    }
}