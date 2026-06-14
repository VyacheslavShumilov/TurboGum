package com.vshum.turbogum.ui.favourite_list

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.FragmentFavouriteBinding
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorParamLinerFav
import com.vshum.turbogum.navigator.Screen
import com.vshum.turbogum.services.Api
import com.vshum.turbogum.ui.favourite.adapter.AdapterLinersFavList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteListFragment : Fragment(), AdapterLinersFavList.OnClickListener {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private lateinit var appNavigatorParamLinerFav: AppNavigatorParamLinerFav
    private lateinit var appDao: LinersDao

    private var allCatalog: List<Liner> = emptyList()
    private var allFavourites: List<LinersFavourite> = emptyList()
    private var seriesList: List<String> = emptyList()
    private var selectedSeries: String? = null

    private val chipColors = listOf(
        "#E74C3C", // красный
        "#E67E22", // оранжевый
        "#27AE60", // зелёный
        "#2980B9", // синий
        "#8E44AD", // фиолетовый
        "#16A085", // бирюзовый
        "#D35400", // тёмно-оранжевый
        "#C0392B", // тёмно-красный
        "#1ABC9C", // мятный
        "#2C3E50", // тёмно-серый
        "#6C5CE7", // индиго
        "#00B894"  // изумрудный
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spanCount = maxOf(3, (resources.displayMetrics.widthPixels / resources.displayMetrics.density / 100).toInt())
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.btnEmptyCta.setOnClickListener { appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN) }
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val favourites = withContext(Dispatchers.IO) { appDao.getAllFavouriteLiners() }
            if (_binding == null) return@launch

            allFavourites = favourites

            if (allFavourites.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.listContainer.visibility = View.GONE
                return@launch
            }

            binding.emptyState.visibility = View.GONE
            binding.listContainer.visibility = View.VISIBLE

            // Загружаем каталог если ещё не загружен
            if (allCatalog.isEmpty()) {
                allCatalog = withContext(Dispatchers.IO) {
                    try { Api.create().getLinersList().execute().body() ?: emptyList() }
                    catch (e: Exception) { emptyList() }
                }
            }
            if (_binding == null) return@launch

            // Серии только из избранных
            val seriesOrder = listOf(
                "Серия 1", "Серия 2", "Серия 3", "Серия 4", "Серия 5",
                "Super 1", "Super 2", "Super 3",
                "Sport 1", "Sport 2",
                "Classic 1", "Classic 2"
            )
            val presentSeries = allFavourites.map { it.series }.distinct().toSet()
            seriesList = seriesOrder.filter { it in presentSeries }
            if (selectedSeries !in seriesList) selectedSeries = seriesList.first()

            setupChips()
            updateGrid()
        }
    }

    private fun setupChips() {
        binding.chipGroup.removeAllViews()
        seriesList.forEach { series ->
            val chip = Chip(requireContext()).apply {
                text = series
                isCheckable = false
                isClickable = true
                isChipIconVisible = false
                chipCornerRadius = 20f * resources.displayMetrics.density
                setOnClickListener {
                    if (selectedSeries != series) {
                        selectedSeries = series
                        applyChipStyles()
                        updateGrid()
                    }
                }
            }
            binding.chipGroup.addView(chip)
        }
        applyChipStyles()
    }

    private fun applyChipStyles() {
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            val color = chipColors[i % chipColors.size]
            if (seriesList.getOrNull(i) == selectedSeries) {
                chip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(color))
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.bg_surface_2)
                chip.setTextColor(Color.parseColor(color))
            }
        }
    }

    private fun updateGrid() {
        val series = selectedSeries ?: return
        // Все вкладыши этой серии из каталога, сортировка по номеру
        val seriesLiners = allCatalog
            .filter { it.series == series }
            .sortedWith(compareBy { it.numberLiner.trimStart('0').toIntOrNull() ?: 0 })
        // Map uniqueNumber -> LinersFavourite для быстрой проверки
        val ownedMap = allFavourites.filter { it.series == series }.associateBy { it.uniqueNumber }
        binding.recyclerView.adapter = AdapterLinersFavList(seriesLiners, ownedMap, this)
    }

    override fun onClickLinerFavorite(linersFav: LinersFavourite) {
        appNavigatorParamLinerFav.navigateToParamLinerFav(Screen.FAVOURITE_LINER_SCREEN, linersFav)
    }

    override fun onResume() {
        super.onResume()
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
        appNavigatorParamLinerFav = app.servicesLocator.providerNavigatorParamLinerFav(requireActivity())
        appDao = app.getDatabase().linersDao()
    }
}
