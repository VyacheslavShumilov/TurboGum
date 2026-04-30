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
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentWrappersListBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorParamWrapper
import com.vshum.turbogum.navigator.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Home / Wrappers List screen.
 * Shows: header with bars logo, hero, stats card, series grid.
 */
class WrappersListFragment : Fragment() {

    private var _binding: FragmentWrappersListBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private lateinit var appNavigatorParamWrapper: AppNavigatorParamWrapper

    /**
     * Series catalog. Maps series key → display label, range, year, image, gradient.
     */
    private data class SeriesEntry(
        val seriesKey: String,
        val label: String,
        val range: String,
        val year: String,
        val badge: String,
        val imageRes: Int,
        val gradientRes: Int,
        val screen: Screen
    )

    private val seriesEntries: List<SeriesEntry> by lazy {
        listOf(
            SeriesEntry("series1", "Серия 1", "№ 1–50",   "1989", "S1", R.drawable.t1, R.drawable.series_1_gradient, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series2", "Серия 2", "№ 51–120", "1990", "S2", R.drawable.t2, R.drawable.series_2_gradient, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series3", "Серия 3", "№ 121–190","1991", "S3", R.drawable.t3, R.drawable.series_3_gradient, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series4", "Серия 4", "№ 191–260","1992", "S4", R.drawable.t4, R.drawable.series_4_gradient, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series5", "Серия 5", "№ 261–330","1993", "S5", R.drawable.t5, R.drawable.series_5_gradient, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super1",  "Super 1", "№ 1–70",   "1993", "SU", R.drawable.t6, R.drawable.series_super_gradient, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super2",  "Super 2", "№ 71–140", "1994", "SU", R.drawable.t7, R.drawable.series_super_gradient, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super3",  "Super 3", "№ 141–210","1995", "SU", R.drawable.t8, R.drawable.series_super_gradient, Screen.LINERS_LIST_SCREEN)
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

        buildSeriesGrid()
        loadStats()
    }

    private fun loadStats() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = (requireContext().applicationContext as App)
                .getDatabase().linersDao()
            val count = try { dao.getAllFavouriteLiners().size } catch (e: Exception) { 0 }
            val total = 600 // approximate total stickers across all series
            val percent = if (total > 0) (count * 100 / total) else 0
            withContext(Dispatchers.Main) {
                binding.statCount.text = count.toString()
                binding.statProgress.text = "$percent%"
            }
        }
    }

    private fun buildSeriesGrid() {
        val grid = binding.seriesGrid
        grid.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        val gap = (8f * resources.displayMetrics.density).toInt()

        seriesEntries.forEach { entry ->
            val cardView = inflater.inflate(R.layout.item_series_card, grid, false)
                    as MaterialCardView

            val spec = GridLayout.LayoutParams().apply {
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                width = 0
                setMargins(gap, gap, gap, gap)
            }
            cardView.layoutParams = spec

            // Background gradient per series
            cardView.findViewById<View>(R.id.seriesBackground)
                ?.setBackgroundResource(entry.gradientRes)

            // Image with shimmer
            val imageView = cardView.findViewById<ImageView>(R.id.seriesImage)
            val shimmer = cardView.findViewById<ShimmerFrameLayout>(R.id.shimmerLayout)
            imageView?.setImageResource(entry.imageRes)
            shimmer?.stopShimmer()
            shimmer?.visibility = View.GONE
            imageView?.visibility = View.VISIBLE

            cardView.findViewById<TextView>(R.id.seriesLabel)?.text = entry.label
            cardView.findViewById<TextView>(R.id.seriesRange)?.text = entry.range
            cardView.findViewById<TextView>(R.id.seriesYear)?.text = entry.year
            cardView.findViewById<TextView>(R.id.seriesBadge)?.text = entry.badge

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