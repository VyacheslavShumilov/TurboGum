package com.vshum.turbogum.ui.favourite_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vshum.turbogum.App
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.FragmentFavouriteBinding
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.AppNavigatorParamLinerFav
import com.vshum.turbogum.navigator.Screen
import com.vshum.turbogum.ui.favourite_list.adapter.AdapterLinersFavList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Favourites screen.
 * Shows: app header, count subtitle, list of favourite stickers,
 * or empty state with CTA when nothing collected yet.
 */
class FavouriteListFragment :
    Fragment(),
    AdapterLinersFavList.OnClickListener {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private lateinit var appNavigatorParamLinerFav: AppNavigatorParamLinerFav
    private lateinit var appDao: LinersDao

    private val data = ArrayList<LinersFavourite>()
    private lateinit var adapter: AdapterLinersFavList

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
        loadData()
    }

    private fun setupRecycler() {
        adapter = AdapterLinersFavList(data, this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@FavouriteListFragment.adapter
        }
    }

    private fun setupEmptyCta() {
        binding.btnEmptyCta.setOnClickListener {
            appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
        }
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val items = try {
                appDao.getAllFavouriteLiners()
            } catch (e: Exception) {
                emptyList()
            }
            withContext(Dispatchers.Main) {
                data.clear()
                data.addAll(items)
                adapter.notifyDataSetChanged()
                showState(items.isEmpty())
            }
        }
    }

    private fun showState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.listContainer.visibility = if (isEmpty) View.GONE else View.VISIBLE
        if (!isEmpty) {
            binding.favCount.text = countText(data.size)
        }
    }

    private fun countText(count: Int): String {
        // Simple Russian pluralisation: 1 карточка / 2-4 карточки / 5+ карточек
        val mod10 = count % 10
        val mod100 = count % 100
        val word = when {
            mod100 in 11..14 -> "карточек"
            mod10 == 1 -> "карточка"
            mod10 in 2..4 -> "карточки"
            else -> "карточек"
        }
        return "$count $word"
    }

    // ── Adapter callbacks ─────────────────────────────────────────────

    override fun onDeleteFavorite(linersFav: LinersFavourite) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDao.deleteFavoriteLiner(linersFav)
            } catch (e: Exception) {
                // ignore
            }
            withContext(Dispatchers.Main) {
                binding.favCount.text = countText(data.size)
            }
        }
    }

    override fun notFavorite() {
        showState(true)
    }

    override fun onClickLinerFavorite(linersFav: LinersFavourite) {
        appNavigatorParamLinerFav.navigateToParamLinerFav(
            Screen.FAVOURITE_LINER_SCREEN, linersFav
        )
    }

    // ── Lifecycle ─────────────────────────────────────────────────────

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
        appNavigatorParamLinerFav =
            app.servicesLocator.providerNavigatorParamLinerFav(requireActivity())
        appDao = app.getDatabase().linersDao()
    }
}