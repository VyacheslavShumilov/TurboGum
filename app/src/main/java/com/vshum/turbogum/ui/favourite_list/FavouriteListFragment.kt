package com.vshum.turbogum.ui.favourite_list

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
import com.vshum.turbogum.navigator.AppNavigatorParamLinerFav
import com.vshum.turbogum.navigator.ScreenParamLinerFav
import com.vshum.turbogum.ui.favourite_list.adapter.AdapterLinersFavList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavouriteListFragment : Fragment(), AdapterLinersFavList.OnClickListener {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var appDao: LinersDao
    private lateinit var adapterLinersFav: AdapterLinersFavList
    private lateinit var appNavigator: AppNavigatorParamLinerFav
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
        appNavigator = (context?.applicationContext as App).servicesLocator.providerNavigatorParamLinerFav(requireActivity())

        showProgress(true)
        appDao = (context?.applicationContext as App).getDatabase().linersDao()
        lifecycleScope.launch(Dispatchers.IO) {
            favorite.addAll(appDao.getAllFavouriteLiners())
            showProgress(false)
            adapterLinersFav = AdapterLinersFavList(favorite, this@FavouriteListFragment)
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

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }


    override fun onClickLinerFavorite(linersFav: LinersFavourite) {
        appNavigator.navigateToParamLinerFav(ScreenParamLinerFav.FAVORITE_LINER, linersFav)
    }

    override fun onResume() {
        super.onResume()

        /*** Удаление дубликатов из списка при возврате из FavoriteLinerFragment
         * Чтобы исправить проблему, можно пересоздать адаптер в методе onResume, чтобы он обновился с новыми данными, а затем присвоить его списку. Также можно добавить проверку, чтобы адаптер не создавался заново, если он уже создан.
         * сли адаптер уже инициализирован, мы вызываем метод updateData с новым списком данных для обновления адаптера. Если адаптер не инициализирован, мы создаем новый адаптер с новым списком данных и присваиваем его списку.
         */

        val uniqueList = (favorite.distinctBy { it.numberLiner}) as ArrayList

        if (::adapterLinersFav.isInitialized) {
            favorite.clear()
            adapterLinersFav.updateData(uniqueList)
        } else {
            adapterLinersFav = AdapterLinersFavList(uniqueList, this@FavouriteListFragment)
            binding.recyclerView.adapter = adapterLinersFav
        }
    }
}