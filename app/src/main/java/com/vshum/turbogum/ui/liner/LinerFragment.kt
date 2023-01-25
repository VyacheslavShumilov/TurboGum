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
import com.vshum.turbogum.dao.AppDatabase
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.FragmentLinerBinding
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LinerFragment(var liner: Liner) : Fragment() {
    private lateinit var binding: FragmentLinerBinding
    private lateinit var appNavigator: AppNavigator
    lateinit var appDao: LinersDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLinerBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Picasso.get().load(liner.imageUrlLiner).into(binding.imageView)
        appDao = (context?.applicationContext as App).getDatabase().linersDao()


        with(binding) {
            linkVideo.setOnClickListener {
                if (liner.video != "-") {
                    val uri: Uri = Uri.parse(liner.video)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireActivity(), "Видео отсуствует", Toast.LENGTH_SHORT).show()
                }
            }

            linkVk.setOnClickListener {
                if (liner.vkArticle != "-") {
                    val uri: Uri = Uri.parse(liner.vkArticle)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireActivity(), "Статья VK в разработке", Toast.LENGTH_SHORT)
                        .show()
                }

            }
            linkWiki.setOnClickListener {
                if (liner.wikiArticle != "-") {
                    val uri: Uri = Uri.parse(liner.wikiArticle)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireActivity(), "Статья отсутствует", Toast.LENGTH_SHORT).show()
                }
            }

            btnAddFavourite.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val linerFavourite = LinersFavourite(
                        0,
                        liner.id,
                        liner.numberLiner,
                        liner.brand,
                        liner.model,
                        liner.wikiArticle,
                        liner.video,
                        liner.vkArticle,
                        liner.imageUrlLiner,
                        liner.nameWrapper
                    )
                }
                btnAddFavourite.visibility = View.GONE
                btnDeleteFavourite.visibility = View.VISIBLE


            }

            binding.btnToFavourite.setOnClickListener {
                appNavigator.navigateTo(Screen.FAVOURITE)
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator = (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }
}