package com.vshum.turbogum.ui.wrappers_list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentWrappersListBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorParamWrapper
import com.vshum.turbogum.navigator.Screen
import com.vshum.turbogum.services.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Home / Wrappers list screen.
 * Loads sticker data from GitHub Raw via Retrofit (same source as before).
 * Shows: stats card (Серий / Вкладышей / Собрано) + 12-series grid.
 */
class WrappersListFragment : Fragment() {

    private var _binding: FragmentWrappersListBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private lateinit var appNavigatorParamWrapper: AppNavigatorParamWrapper

    private val api = Api.create()

    private data class SeriesEntry(
        val seriesKey: String,
        val seriesName: String,
        val label: String,
        val year: String,
        val rangeFrom: Int,
        val rangeTo: Int,
        val imageRes: Int,
        val screen: Screen
    )

    private val seriesEntries: List<SeriesEntry> by lazy {
        listOf(
            SeriesEntry("series1",  "Серия 1",   "Серия 1",   "1989",   1,  50, R.drawable.t1,  Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series2",  "Серия 2",   "Серия 2",   "1990",  51, 120, R.drawable.t2,  Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series3",  "Серия 3",   "Серия 3",   "1991", 121, 190, R.drawable.t3,  Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series4",  "Серия 4",   "Серия 4",   "1992", 191, 260, R.drawable.t4,  Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series5",  "Серия 5",   "Серия 5",   "1993", 261, 330, R.drawable.t5,  Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super1",   "Super 1",   "Super 1",   "1993", 331, 400, R.drawable.t6,  Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super2",   "Super 2",   "Super 2",   "1994", 401, 470, R.drawable.t7,  Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super3",   "Super 3",   "Super 3",   "1995", 471, 540, R.drawable.t8,  Screen.LINERS_LIST_SCREEN),
            SeriesEntry("sport1",   "Sport 1",   "Sport 1",   "1996",   1,  70, R.drawable.ts1, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("sport2",   "Sport 2",   "Sport 2",   "1997",  71, 140, R.drawable.ts2, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("classic1", "Classic 1", "Classic 1", "1998",   1,  70, R.drawable.tc1, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("classic2", "Classic 2", "Classic 2", "1999",  71, 140, R.drawable.tc2, Screen.LINERS_LIST_SCREEN)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWrappersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Build the grid immediately from static catalogue (no network needed)
        buildSeriesGrid()
        // Load counts + owned from network + DB
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            // 1. Load liners from GitHub via Retrofit
            val all = try {
                val response = api.getLinersList().execute()
                if (response.isSuccessful) response.body() ?: emptyList()
                else {
                    Log.e("WrappersListFragment", "HTTP ${response.code()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("WrappersListFragment", "Network error", e)
                emptyList()
            }

            val perSeriesCounts = all.groupingBy { it.series.trim() }.eachCount()
            val totalLiners = all.size

            // 2. Owned count from DB
            val owned = try {
                (requireContext().applicationContext as App)
                    .getDatabase().linersDao().getAllFavouriteLiners().size
            } catch (e: Exception) { 0 }

            withContext(Dispatchers.Main) {
                bindStats(owned, totalLiners)
                updateSeriesGridCounts(perSeriesCounts)
            }
        }
    }

    private fun bindStats(ownedCount: Int, totalLiners: Int) {
        binding.statSeriesCount.text = seriesEntries.size.toString()
        binding.statTotalCount.text  = totalLiners.toString()
        binding.statCollected.text   = "$ownedCount / $totalLiners"
    }

    /**
     * Build the grid once from the static catalogue.
     * Range labels are shown immediately (no network needed).
     */
    private fun buildSeriesGrid() {
        val grid = binding.seriesGrid
        grid.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        val gap = (6f * resources.displayMetrics.density).toInt()

        seriesEntries.forEach { entry ->
            val cardView = inflater.inflate(R.layout.item_series_card, grid, false)
                    as MaterialCardView

            val spec = GridLayout.LayoutParams().apply {
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                width = 0
                setMargins(gap, gap, gap, gap)
            }
            cardView.layoutParams = spec

            cardView.tag = entry.seriesName  // used by updateSeriesGridCounts

            cardView.findViewById<ImageView>(R.id.seriesImage)?.setImageResource(entry.imageRes)
            cardView.findViewById<TextView>(R.id.seriesBadge)?.text  = entry.label
            cardView.findViewById<TextView>(R.id.seriesYear)?.text   = entry.year
            // Range label shown immediately
            cardView.findViewById<TextView>(R.id.seriesRange)?.text  =
                "№ ${entry.rangeFrom}–${entry.rangeTo}"

            cardView.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(entry.screen, entry.seriesKey)
            }
            grid.addView(cardView)
        }
    }

    /**
     * After network load — update count labels on cards.
     * Range labels stay; we update seriesRange to show count if needed,
     * or leave it as "№ from–to" (current design decision: keep range).
     */
    private fun updateSeriesGridCounts(perSeriesCounts: Map<String, Int>) {
        // Range labels are already set in buildSeriesGrid.
        // This method is a hook if you want to show owned counts later.
        // Currently no-op: range label is preferred over count.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val app = context.applicationContext as App
        appNavigator             = app.servicesLocator.providerNavigator(requireActivity())
        appNavigatorParamWrapper = app.servicesLocator.providerNavigatorParamWrapper(requireActivity())
    }
}