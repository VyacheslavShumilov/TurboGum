package com.vshum.turbogum.ui.liners_lists.contract

import android.content.Context
import android.util.Log
import com.vshum.turbogum.Constants
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.services.Api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Loads stickers for a given series from GitHub Raw via Retrofit.
 * URL: https://raw.githubusercontent.com/VyacheslavShumilov/Base/main/dataLiners.json
 *
 * The network call runs on Dispatchers.IO; the result is delivered
 * to the View on Dispatchers.Main.
 */
class LinersListPresenterImpl(
    private val view: LinersListContract.View,
    private val context: Context
) : LinersListContract.Presenter {

    private val api = Api.create()

    override fun startScreen(seriesKey: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val all = loadAllLiners()
            val filtered = all.filter { matches(it, seriesKey) }
            withContext(Dispatchers.Main) {
                view.onSuccessList(filtered)
            }
        }
    }

    private fun loadAllLiners(): List<Liner> {
        return try {
            val response = api.getLinersList().execute()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("LinersPresenter", "HTTP ${response.code()} loading liners")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("LinersPresenter", "Failed to load liners from network", e)
            emptyList()
        }
    }

    private fun matches(liner: Liner, seriesKey: String): Boolean {
        val series = liner.series.trim()
        return when (seriesKey) {
            "series1"  -> series == "Серия 1"
            "series2"  -> series == "Серия 2"
            "series3"  -> series == "Серия 3"
            "series4"  -> series == "Серия 4"
            "series5"  -> series == "Серия 5"
            "super1"   -> series == "Super 1"
            "super2"   -> series == "Super 2"
            "super3"   -> series == "Super 3"
            "sport1"   -> series == "Sport 1"
            "sport2"   -> series == "Sport 2"
            "classic1" -> series == "Classic 1"
            "classic2" -> series == "Classic 2"
            else -> false
        }
    }
}