package com.vshum.turbogum.ui.favorite_liner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.FragmentFavoriteLinerBinding
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavoriteLinerFragment(var linerFav: LinersFavourite) : Fragment() {
    private lateinit var binding: FragmentFavoriteLinerBinding
    private lateinit var appDao: LinersDao
    private var addedNote: String = ""
    private lateinit var appNavigator: AppNavigator


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteLinerBinding.inflate(inflater, container, false)

        /***
         * Нужно инициализировать свойство "linersDao" перед его использованием. Одним из способов сделать это является инициализация свойства в методе "onCreateView" до того, как вы вызываете метод "launch" в блоке "lifecycleScope"
         */

        appDao = (context?.applicationContext as App).getDatabase().linersDao()

        initIcons()
        initTextViews()





        // Извлекаем заметку из базы данных
        lifecycleScope.launch(Dispatchers.IO) {
            addedNote = appDao.getNoteLiner(linerFav.uniqueNumber)
            withContext(Dispatchers.Main) {
                if (addedNote != "-") {
                    binding.noteTxtView.text =
                        addedNote // устанавливаем значение в поле noteTxtView
                } else addedNote = "Заметка отсутствует"
            }
        }


        binding.toolbar.toWrappersBtn.setOnClickListener {
            appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
        }




        binding.linkVideo.setOnClickListener {
            if (linerFav.video != "-") {
                val uri: Uri = Uri.parse(linerFav.video)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        binding.linkVk.setOnClickListener {
            if (linerFav.vkArticle != "-") {
                val uri: Uri = Uri.parse(linerFav.vkArticle)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        binding.linkWiki.setOnClickListener {
            if (linerFav.wikiArticle != "-") {
                val uri: Uri = Uri.parse(linerFav.wikiArticle)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        binding.saveNoteBtn.setOnClickListener {
            val note = binding.noteInput.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                appDao.editNoteLiner(linerFav.uniqueNumber, note)
                withContext(Dispatchers.Main) {
                    binding.noteTxtView.text = note
                }
            }
        }

        return binding.root
    }

    private fun initIcons() {
        with(binding) {

            toolbar.toFavouriteBtn.visibility = View.GONE

            if (linerFav.video == "-") {
                linkVideo.visibility = View.GONE
            }
            if (linerFav.vkArticle == "-") {
                linkVk.visibility = View.GONE
            }
            if (linerFav.wikiArticle == "-") {
                linkWiki.visibility = View.GONE
            }

//            if (liner.websiteSociete == "-") {
//                websiteSociete.visibility = View.GONE
//            }

            if (linerFav.imageUrlLiner.isEmpty()) {
                binding.imageView.setImageResource(R.drawable.placeholder2)
            } else {
                Picasso.get().load(linerFav.imageUrlLiner).into(binding.imageView)
            }
        }
    }

    private fun initTextViews() {
        with(binding) {
            linerIndex.text = linerFav.index
            linerNumber.text = linerFav.numberLiner
            linerBrand.text = linerFav.brand
            linerModel.text = linerFav.model
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("noteText", binding.noteTxtView.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val noteText = savedInstanceState?.getString("noteText")
        binding.noteTxtView.text = noteText
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }


}