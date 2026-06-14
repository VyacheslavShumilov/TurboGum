package com.vshum.turbogum.ui.favourite_list.impl

import android.util.Log
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.model.SeriesStats
import com.vshum.turbogum.model.toCatalogEntity
import com.vshum.turbogum.services.Api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "FavouriteList"

/** Loads the user's favourites and groups them into per-series sections (one section per series in [SeriesStats], with placeholders for not-yet-collected numbers). */
class FavouriteListPresenterImpl(
    private val view: FavouriteListContract.View,
    private val appDao: LinersDao
) : FavouriteListContract.Presenter {

    private var job: Job? = null
    private var allSections: List<CollectionSection> = emptyList()
    private var selectedSeriesKey: String? = null

    override fun loadData() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            ensureCatalogCached()

            val favourites = try {
                appDao.getAllFavouriteLiners()
            } catch (e: Exception) {
                Log.e(TAG, "getAllFavouriteLiners() failed", e)
                emptyList()
            }
            Log.d(TAG, "favourites loaded: ${favourites.size}")

            val favouritesBySeries = favourites.groupBy { it.series.trim() }

            val sections = SeriesStats.all.map { stat ->
                val seriesFavourites = favouritesBySeries[stat.ruName] ?: emptyList()
                val byNumber = seriesFavourites.associateBy { numberOf(it.numberLiner) }

                val stickers = (1..stat.total).map { number ->
                    CollectionSticker(stat.key, number, byNumber[number])
                }

                CollectionSection(
                    seriesKey = stat.key,
                    title = "${stat.label} · ${byNumber.size} из ${stat.total}",
                    stickers = stickers
                )
            }

            withContext(Dispatchers.Main) {
                allSections = sections
                Log.d(TAG, "sections built: ${sections.size}")
                if (favourites.isEmpty()) {
                    view.onEmpty()
                } else {
                    emitFiltered()
                }
            }
        }
    }

    /** Populates the [com.vshum.turbogum.model.LinerCatalog] Room cache from the network on first use. */
    private suspend fun ensureCatalogCached() {
        val cached = try {
            appDao.getAllLiners()
        } catch (e: Exception) {
            Log.e(TAG, "getAllLiners() failed", e)
            emptyList()
        }
        Log.d(TAG, "catalog cached: ${cached.size}")
        if (cached.isNotEmpty()) return

        try {
            val response = Api.create().getLinersList().execute()
            val liners = response.body() ?: emptyList()
            Log.d(TAG, "catalog fetched from network: ${liners.size}, code=${response.code()}")
            if (liners.isNotEmpty()) {
                appDao.insertAllCatalog(liners.map { it.toCatalogEntity() })
            }
        } catch (e: Exception) {
            Log.e(TAG, "catalog fetch failed", e)
        }
    }

    override fun selectFilter(seriesKey: String?) {
        selectedSeriesKey = seriesKey
        emitFiltered()
    }

    private fun emitFiltered() {
        val chips = listOf(ChipData(null, "Все")) +
            SeriesStats.all.map { ChipData(it.key, it.label) }

        val filtered = selectedSeriesKey
            ?.let { key -> allSections.filter { it.seriesKey == key } }
            ?: allSections

        Log.d(TAG, "emitting ${filtered.size} sections, ${filtered.sumOf { it.stickers.size }} stickers total")
        view.onData(filtered, chips)
    }

    private fun numberOf(numberLiner: String): Int =
        numberLiner.filter { it.isDigit() }.toIntOrNull() ?: 0

    override fun onDestroy() {
        job?.cancel()
    }
}
