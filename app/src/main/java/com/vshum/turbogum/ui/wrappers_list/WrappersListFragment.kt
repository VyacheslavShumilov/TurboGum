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
import com.google.android.material.card.MaterialCardView
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentWrappersListBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorParamWrapper
import com.vshum.turbogum.navigator.Screen
import com.vshum.turbogum.navigator.ScreenParamWrapper

class WrappersListFragment : Fragment() {

    private lateinit var binding: FragmentWrappersListBinding
    private lateinit var appNavigator: AppNavigator
    private lateinit var appNavigatorParamWrapper: AppNavigatorParamWrapper

    // Каждый элемент: (название серии, строка диапазона, drawable обёртки, ScreenParamWrapper, series key)
    private data class SeriesEntry(
        val label: String,
        val range: String,
        val imageRes: Int,
        val screen: ScreenParamWrapper,
        val seriesKey: String
    )

    private val seriesEntries by lazy {
        listOf(
            SeriesEntry("Серия 1",  "Nos. 1–50",    R.drawable.t1,  ScreenParamWrapper.SERIES_1,  "Серия 1"),
            SeriesEntry("Серия 2",  "Nos. 51–120",  R.drawable.t2,  ScreenParamWrapper.SERIES_2,  "Серия 2"),
            SeriesEntry("Серия 3",  "Nos. 121–190", R.drawable.t3,  ScreenParamWrapper.SERIES_3,  "Серия 3"),
            SeriesEntry("Серия 4",  "Nos. 191–260", R.drawable.t4,  ScreenParamWrapper.SERIES_4,  "Серия 4"),
            SeriesEntry("Серия 5",  "Nos. 261–330", R.drawable.t5,  ScreenParamWrapper.SERIES_5,  "Серия 5"),
            SeriesEntry("Super 1",  "Nos. 1–70",    R.drawable.t6,  ScreenParamWrapper.SUPER_1,   "Super 1"),
            SeriesEntry("Super 2",  "Nos. 71–140",  R.drawable.t7,  ScreenParamWrapper.SUPER_2,   "Super 2"),
            SeriesEntry("Super 3",  "Nos. 141–210", R.drawable.t8,  ScreenParamWrapper.SUPER_3,   "Super 3"),
            SeriesEntry("Sport 1",  "Nos. 1–70",    R.drawable.ts1, ScreenParamWrapper.SPORT_1,   "Sport 1"),
            SeriesEntry("Sport 2",  "Nos. 71–140",  R.drawable.ts2, ScreenParamWrapper.SPORT_2,   "Sport 2"),
            SeriesEntry("Classic 1","Nos. 1–70",    R.drawable.tc1, ScreenParamWrapper.CLASSIC_1, "Classic 1"),
            SeriesEntry("Classic 2","Nos. 71–140",  R.drawable.tc2, ScreenParamWrapper.CLASSIC_2, "Classic 2")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWrappersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toFavouriteBtn.setOnClickListener {
            appNavigator.navigateTo(Screen.FAVOURITE)
        }


        buildSeriesGrid()
    }

    private fun buildSeriesGrid() {
        val grid = binding.seriesGrid
        grid.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        seriesEntries.forEach { entry ->
            val cardView = inflater.inflate(
                R.layout.item_series_card, grid, false
            ) as MaterialCardView

            val spec = GridLayout.LayoutParams().apply {
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                width = 0
                setMargins(4, 4, 4, 4)
            }
            cardView.layoutParams = spec

            // Устанавливаем данные в карточку
            cardView.findViewById<ImageView>(R.id.seriesImage)?.setImageResource(entry.imageRes)
            cardView.findViewById<TextView>(R.id.seriesLabel)?.text = entry.label
            cardView.findViewById<TextView>(R.id.seriesCount)?.text = entry.range
            cardView.findViewById<TextView>(R.id.seriesChip)?.text = entry.label

            cardView.setOnClickListener {
                appNavigatorParamWrapper.navigateToParamWrapper(entry.screen, entry.seriesKey)
            }

            grid.addView(cardView)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
        appNavigatorParamWrapper =
            (context.applicationContext as App).servicesLocator.providerNavigatorParamWrapper(requireActivity())
    }
}