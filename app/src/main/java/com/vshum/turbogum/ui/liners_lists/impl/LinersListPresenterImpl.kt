package com.vshum.turbogum.ui.liners_lists.contract

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vshum.turbogum.model.Liner

/**
 * Loads stickers for a given series from `assets/dataLiners.json`
 * (or from wherever the project was loading them previously).
 *
 * The fragment passes a series key like "series1", "super1" — we
 * filter the global list by liner.series matching the human name
 * for that key.
 */
class LinersListPresenterImpl(
    private val view: LinersListContract.View,
    private val context: Context
) : LinersListContract.Presenter {

    override fun startScreen(seriesKey: String) {
        val all = loadAllLiners()
        val filtered = all.filter { matches(it, seriesKey) }
        view.onSuccessList(filtered)
    }

    /** Read full sticker list from assets/dataLiners.json. */
    private fun loadAllLiners(): List<Liner> {
        return try {
            val json = context.assets.open("dataLiners.json")
                .bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Liner>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** Match a liner to a series key via its `series` field. */
    private fun matches(liner: Liner, seriesKey: String): Boolean {
        val series = liner.series.trim()
        return when (seriesKey) {
            "series1" -> series == "Серия 1"
            "series2" -> series == "Серия 2"
            "series3" -> series == "Серия 3"
            "series4" -> series == "Серия 4"
            "series5" -> series == "Серия 5"
            "super1" -> series == "Super 1"
            "super2" -> series == "Super 2"
            "super3" -> series == "Super 3"
            else -> false
        }
    }
}