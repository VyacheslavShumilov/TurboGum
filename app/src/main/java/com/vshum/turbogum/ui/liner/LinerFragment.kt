package com.vshum.turbogum.ui.liner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentLinerBinding
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen
import com.vshum.turbogum.navigator.ScreenParamWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LinerFragment(var liner: Liner) : Fragment() {
    private lateinit var binding: FragmentLinerBinding
    private lateinit var appNavigator: AppNavigator


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLinerBinding.inflate(inflater, container, false)

        if (liner.video == "-") binding.linkVideo.visibility = View.GONE
        if (liner.vkArticle == "-") binding.linkVk.visibility = View.GONE
        if (liner.wikiArticle == "-") binding.linkWiki.visibility = View.GONE

        if (liner.imageUrlLiner.isEmpty()) {
            binding.imageView.setImageResource(R.drawable.placeholder2)
        } else {
            Picasso.get().load(liner.imageUrlLiner).into(binding.imageView)
        }

        binding.linkVideo.setOnClickListener {
            if (liner.video != "-") {
                val uri: Uri = Uri.parse(liner.video)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        binding.linkVk.setOnClickListener {
            if (liner.vkArticle != "-") {
                val uri: Uri = Uri.parse(liner.vkArticle)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        binding.linkWiki.setOnClickListener {
            if (liner.wikiArticle != "-") {
                val uri: Uri = Uri.parse(liner.wikiArticle)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        binding.btnAddFavourite.setImageDrawable(resources.getDrawable(R.drawable.btn_fav_border))

        binding.btnToFavourite.setOnClickListener {
            appNavigator.navigateTo(Screen.FAVOURITE)
        }

        binding.btnAddFavourite.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val linerFavourite = LinersFavourite(
                    0,
                    liner.uniqueNumber,
                    liner.id,
                    liner.numberLiner,
                    liner.brand,
                    liner.model,
                    liner.wikiArticle,
                    liner.video,
                    liner.vkArticle,
                    liner.imageUrlLiner,
                    liner.index,
                    liner.series,
                    liner.note
                )
                (context?.applicationContext as App).getDatabase().linersDao()
                    .insertLiner(linerFavourite)
            }
            binding.btnAddFavourite.setImageDrawable(resources.getDrawable(R.drawable.btn_fav_filled))
            binding.btnAddFavourite.isClickable = false
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val linerFav = (context?.applicationContext as App).getDatabase().linersDao().getLinerFavorite(liner.uniqueNumber)
            linerFav.let { data ->
                withContext(Dispatchers.Main) {
                    @Suppress("SENSELESS_COMPARISON")
                    binding.btnAddFavourite.isClickable = data == null
                    if (binding.btnAddFavourite.isClickable) {
                        binding.btnAddFavourite.setImageDrawable(resources.getDrawable(R.drawable.btn_fav_border))
                    } else {
                        binding.btnAddFavourite.setImageDrawable(resources.getDrawable(R.drawable.btn_fav_filled))
                    }
                }
            }
        }

        return binding.root
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }

}





