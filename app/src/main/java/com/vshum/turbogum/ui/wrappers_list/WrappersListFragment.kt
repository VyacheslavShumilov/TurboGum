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

class WrappersListFragment : Fragment() {

    private var _binding: FragmentWrappersListBinding? = null
    private val binding get() = _binding

    private lateinit var appNavigator: AppNavigator
    private lateinit var appNavigatorParamWrapper: AppNavigatorParamWrapper

    private val api = Api.create()

    private data class SeriesEntry(
        val seriesKey: String,
        val seriesName: String,
        val label: String,
        val rangeFrom: Int,
        val rangeTo: Int,
        val imageRes: Int,
        val screen: Screen
    )

    private val seriesEntries: List<SeriesEntry> by lazy {
        listOf(
            SeriesEntry("series1", "Серия 1", "Серия 1", 1, 50, R.drawable.t1, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series2", "Серия 2", "Серия 2", 51, 120, R.drawable.t2, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series3", "Серия 3", "Серия 3", 121, 190, R.drawable.t3, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series4", "Серия 4", "Серия 4", 191, 260, R.drawable.t4, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("series5", "Серия 5", "Серия 5", 261, 330, R.drawable.t5, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super1", "Super 1", "Super 1", 331, 400, R.drawable.t6, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super2", "Super 2", "Super 2", 401, 470, R.drawable.t7, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("super3", "Super 3", "Super 3", 471, 540, R.drawable.t8, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("sport1", "Sport 1", "Sport 1", 1, 70, R.drawable.ts1, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("sport2", "Sport 2", "Sport 2", 71, 140, R.drawable.ts2, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("classic1", "Classic 1", "Classic 1", 1, 70, R.drawable.tc1, Screen.LINERS_LIST_SCREEN),
            SeriesEntry("classic2", "Classic 2", "Classic 2", 71, 140, R.drawable.tc2, Screen.LINERS_LIST_SCREEN)
        )
    }

    // ───────────────────────── lifecycle ─────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWrappersListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buildSeriesGrid()
        loadData()
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

    // ───────────────────────── data load ─────────────────────────

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            val all = try {
                val response = api.getLinersList().execute()
                if (response.isSuccessful) response.body() ?: emptyList()
                else {
                    Log.e("WrappersList", "HTTP ${response.code()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("WrappersList", "Network error", e)
                emptyList()
            }

            val totalLiners = all.size

            val owned = try {
                (requireContext().applicationContext as App)
                    .getDatabase()
                    .linersDao()
                    .getAllFavouriteLiners()
                    .size
            } catch (e: Exception) {
                0
            }

            withContext(Dispatchers.Main) {
                val b = _binding ?: return@withContext

                bindStats(b, owned, totalLiners)
            }
        }
    }

    // ───────────────────────── UI ─────────────────────────

    private fun bindStats(
        b: FragmentWrappersListBinding,
        owned: Int,
        total: Int
    ) {
        b.statSeriesCount.text = seriesEntries.size.toString()
        b.statTotalCount.text = total.toString()
        b.statCollected.text = "$owned / $total"
    }

    private fun buildSeriesGrid() {
        val b = binding ?: return
        val grid = b.seriesGrid

        grid.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())
        val gap = (6f * resources.displayMetrics.density).toInt()

        seriesEntries.forEach { entry ->

            val card = inflater.inflate(
                R.layout.item_series_card,
                grid,
                false
            ) as MaterialCardView

            val params = GridLayout.LayoutParams().apply {
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                width = 0
                setMargins(gap, gap, gap, gap)
            }

            card.layoutParams = params

            card.findViewById<ImageView>(R.id.seriesImage)
                ?.setImageResource(entry.imageRes)

            card.findViewById<TextView>(R.id.seriesBadge)
                ?.text = entry.label

            card.findViewById<TextView>(R.id.seriesYear)
                ?.visibility = View.GONE

            card.findViewById<TextView>(R.id.seriesRange)
                ?.text = "№ ${entry.rangeFrom}–${entry.rangeTo}"

            card.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(
                    entry.screen,
                    entry.seriesKey
                )
            }

            grid.addView(card)
        }
    }
}