package com.vshum.turbogum.ui.wrappers_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentWrappersListBinding
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorParamWrapper
import com.vshum.turbogum.navigator.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Home / Wrappers List screen.
 * Header → Stats card (Серий / Вкладышей / Собрано) → Series grid.
 */
class WrappersListFragment : Fragment() {

    private var _binding: FragmentWrappersListBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private lateinit var appNavigatorParamWrapper: AppNavigatorParamWrapper

    /** Static catalogue of series for the grid. */
    private data class SeriesEntry(
        val seriesKey: String,
        val seriesName: String,   // matches liner.series in JSON
        val label: String,
        val year: String,
        val imageRes: Int,
        val screen: Screen
    )

    private val seriesEntries: List<SeriesEntry> by lazy {
        listOf(
            SeriesEntry("series1", "Серия 1", "Серия 1", "1989", R.drawable.t1, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series2", "Серия 2", "Серия 2", "1990", R.drawable.t2, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series3", "Серия 3", "Серия 3", "1991", R.drawable.t3, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series4", "Серия 4", "Серия 4", "1992", R.drawable.t4, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series5", "Серия 5", "Серия 5", "1993", R.drawable.t5, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super1",  "Super 1", "Super 1", "1993", R.drawable.t6, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super2",  "Super 2", "Super 2", "1994", R.drawable.t7, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super3",  "Super 3", "Super 3", "1995", R.drawable.t8, Screen.LINERS_LIST_SCREEN)
        )
    }

    /** Counts of stickers per series, computed from `dataLiners.json`. */
    private var perSeriesCounts: Map<String, Int> = emptyMap()
    private var totalLiners: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWrappersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    override fun onResume() {
        super.onResume()
        // Refresh DB count when returning from other screens
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            // 1) Load full liner list from assets
            val all: List<Liner> = try {
                val json = requireContext().assets.open("dataLiners.json")
                    .bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<Liner>>() {}.type
                Gson().fromJson<List<Liner>>(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            perSeriesCounts = all.groupingBy { it.series.trim() }.eachCount()
            totalLiners = all.size

            // 2) Owned count from DB
            val owned = try {
                (requireContext().applicationContext as App)
                    .getDatabase().linersDao().getAllFavouriteLiners().size
            } catch (e: Exception) {
                0
            }

            withContext(Dispatchers.Main) {
                bindStats(owned)
                buildSeriesGrid()
            }
        }
    }

    private fun bindStats(ownedCount: Int) {
        binding.statSeriesCount.text = seriesEntries.size.toString()
        binding.statTotalCount.text = totalLiners.toString()
        binding.statCollected.text = "$ownedCount / $totalLiners"
    }

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

            cardView.findViewById<ImageView>(R.id.seriesImage)
                ?.setImageResource(entry.imageRes)
            cardView.findViewById<TextView>(R.id.seriesBadge)?.text = entry.label
            cardView.findViewById<TextView>(R.id.seriesYear)?.text = entry.year

            val count = perSeriesCounts[entry.seriesName] ?: 0
            cardView.findViewById<TextView>(R.id.seriesRange)?.text = "$count шт"

            cardView.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(entry.screen, entry.seriesKey)
            }

            grid.addView(cardView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val app = context.applicationContext as App
        appNavigator = app.servicesLocator.providerNavigator(requireActivity())
        appNavigatorParamWrapper =
            app.servicesLocator.providerNavigatorParamWrapper(requireActivity())
    }
}