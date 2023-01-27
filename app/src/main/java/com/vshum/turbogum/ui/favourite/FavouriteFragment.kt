package com.vshum.turbogum.ui.favourite

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vshum.turbogum.App
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.FragmentFavouriteBinding
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.ui.favourite.adapter.AdapterLinersFavList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavouriteFragment : Fragment(), AdapterLinersFavList.OnClickListener {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var appDao: LinersDao
    private lateinit var adapterLinersFav: AdapterLinersFavList
    private var favorite: ArrayList<LinersFavourite> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDao = (context?.applicationContext as App).getDatabase().linersDao()

        lifecycleScope.launch(Dispatchers.IO) {
            favorite.addAll(appDao.getAllFavouriteLiners())
            adapterLinersFav = AdapterLinersFavList(favorite, this@FavouriteFragment)
            binding.recyclerView.adapter = adapterLinersFav
            setRecyclerViewAutoFit(binding.recyclerView)


            withContext(Dispatchers.Main) {
                if (favorite.size == 0) {
                    Toast.makeText(requireActivity(), "Список пуст", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onDeleteFavorite(linersFav: LinersFavourite) {
        lifecycleScope.launch(Dispatchers.IO) {
            appDao.deleteFavoriteLiner(linersFav)
        }
    }

    override fun notFavorite() {
        Toast.makeText(requireActivity(), "Нет сохраненных", Toast.LENGTH_SHORT).show()
    }

    //авто подсчет количества элементов по ширине в списке
    private fun setRecyclerViewAutoFit(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        layoutManager.spanCount = calculateNoOfColumns(recyclerView.context)
        recyclerView.layoutManager = layoutManager
    }

    private fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / 180 + 0.5).toInt()
    }

}