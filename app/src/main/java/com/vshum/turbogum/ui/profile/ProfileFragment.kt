package com.vshum.turbogum.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var appNavigator: AppNavigator

    private val seriesList = listOf(
        "Серия 1" to R.color.brand_purple,
        "Серия 2" to R.color.brand_teal,
        "Серия 3" to R.color.brand_orange,
        "Super 1" to R.color.brand_blue,
        "Sport 1" to R.color.brand_orange,
        "Classic 1" to R.color.brand_teal
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadStats(view)
        buildProgressCards(view)

        view.findViewById<MaterialCardView>(R.id.menuSettings)?.setOnClickListener {
            // TODO: настройки
        }
        view.findViewById<MaterialCardView>(R.id.menuNotifications)?.setOnClickListener {
            // TODO: уведомления
        }
        view.findViewById<MaterialCardView>(R.id.menuDevelopers)?.setOnClickListener {
            appNavigator.navigateTo(Screen.DEVELOPERS_SCREEN)
        }
    }

    private fun loadStats(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = (context?.applicationContext as App).getDatabase().linersDao()
            val total = dao.getAllFavouriteLiners().size
            withContext(Dispatchers.Main) {
                view.findViewById<TextView>(R.id.statFavourites)?.text = total.toString()
            }
        }
    }

    private fun buildProgressCards(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.progressList) ?: return
        container.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        seriesList.forEach { (name, colorRes) ->
            val item = inflater.inflate(R.layout.item_progress_series, container, false)
            item.findViewById<TextView>(R.id.seriesName).text = name
            item.findViewById<ProgressBar>(R.id.seriesProgressBar).apply {
                progressTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
                progress = 0
            }
            container.addView(item)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }
}