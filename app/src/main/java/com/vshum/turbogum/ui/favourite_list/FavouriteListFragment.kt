package com.vshum.turbogum.ui.favourite_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.FragmentFavouriteBinding
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorParamLinerFav
import com.vshum.turbogum.navigator.Screen
import com.vshum.turbogum.ui.favourite_list.adapter.AdapterCollection
import com.vshum.turbogum.ui.favourite_list.impl.ChipData
import com.vshum.turbogum.ui.favourite_list.impl.CollectionSection
import com.vshum.turbogum.ui.favourite_list.impl.FavouriteListContract
import com.vshum.turbogum.ui.favourite_list.impl.FavouriteListPresenterImpl

/**
 * "Моя коллекция" screen.
 * Series filter chips + grid of all stickers per series (owned shown via Picasso,
 * missing shown as gray placeholders with their number), grouped under section headers.
 */
class FavouriteListFragment :
    Fragment(),
    FavouriteListContract.View,
    AdapterCollection.OnClickListener {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private lateinit var appNavigatorParamLinerFav: AppNavigatorParamLinerFav
    private lateinit var appDao: LinersDao
    private lateinit var presenter: FavouriteListContract.Presenter

    private var adapter: AdapterCollection? = null
    private var spanCount = 3
    private var currentFilterKey: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupEmptyCta()

        presenter = FavouriteListPresenterImpl(this, appDao)
        presenter.loadData()
    }

    private fun setupRecycler() {
        spanCount = computeSpanCount()
        val layoutManager = GridLayoutManager(requireContext(), spanCount)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val isHeader = adapter?.isHeader(position) ?: false
                return if (isHeader) spanCount else 1
            }
        }
        binding.recyclerView.layoutManager = layoutManager
    }

    private fun computeSpanCount(): Int {
        val widthDp = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        return maxOf(3, (widthDp / 100).toInt())
    }

    private fun setupEmptyCta() {
        binding.btnEmptyCta.setOnClickListener {
            appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
        }
    }

    private fun setupChips(chips: List<ChipData>) {
        if (chips.none { it.key == currentFilterKey }) currentFilterKey = null

        binding.filterChipGroup.setOnCheckedStateChangeListener(null)
        binding.filterChipGroup.removeAllViews()

        chips.forEach { chip ->
            val chipView = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = chip.label
                tag = chip.key
                isCheckable = true
                isChecked = chip.key == currentFilterKey
                setChipBackgroundColorResource(R.color.bg_surface_1)
                setChipStrokeColorResource(R.color.border_1)
                chipStrokeWidth = 0.5f
            }
            binding.filterChipGroup.addView(chipView)
        }

        binding.filterChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            currentFilterKey = group.findViewById<Chip>(checkedId).tag as String?
            presenter.selectFilter(currentFilterKey)
        }
    }

    // ── Presenter callbacks ─────────────────────────────────────────────

    override fun onData(sections: List<CollectionSection>, chips: List<ChipData>) {
        if (_binding == null) return

        binding.emptyState.visibility = View.GONE
        binding.listContainer.visibility = View.VISIBLE

        setupChips(chips)

        val current = adapter
        if (current == null) {
            adapter = AdapterCollection(sections, this)
            binding.recyclerView.adapter = adapter
        } else {
            current.update(sections)
        }
    }

    override fun onEmpty() {
        if (_binding == null) return
        binding.emptyState.visibility = View.VISIBLE
        binding.listContainer.visibility = View.GONE
    }

    override fun onClickSticker(favourite: LinersFavourite) {
        appNavigatorParamLinerFav.navigateToParamLinerFav(Screen.FAVOURITE_LINER_SCREEN, favourite)
    }

    // ── Lifecycle ─────────────────────────────────────────────────────

    override fun onResume() {
        super.onResume()
        presenter.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val app = context.applicationContext as App
        appNavigator = app.servicesLocator.providerNavigator(requireActivity())
        appNavigatorParamLinerFav =
            app.servicesLocator.providerNavigatorParamLinerFav(requireActivity())
        appDao = app.getDatabase().linersDao()
    }
}
