package com.vshum.turbogum.ui.favorite_liner

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
//import com.squareup.picasso.Picasso
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
    private lateinit var appNavigator: AppNavigator
    private var addedNote: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteLinerBinding.inflate(inflater, container, false)

        appDao = (context?.applicationContext as App).getDatabase().linersDao()
        initIcons()
        initTextViews()
        initListeners()
        loadNoteFromDatabase()

        return binding.root
    }

    private fun initIcons() {
        with(binding) {
            toolbar.toFavouriteBtn.visibility = View.GONE

            if (linerFav.video == "-") linkVideo.visibility = View.GONE
            if (linerFav.vkArticle == "-") linkVk.visibility = View.GONE
            if (linerFav.wikiArticle == "-") linkWiki.visibility = View.GONE
            if (linerFav.websiteSociete == "-") websiteSociete.visibility = View.GONE

            if (linerFav.imageUrlLiner.isEmpty()) {
                imageView.setImageResource(R.drawable.placeholder)
            } else {
                Glide.with(this@FavoriteLinerFragment)
                    .load(linerFav.imageUrlLiner)
                    .into(imageView)
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

    private fun initListeners() {
        // Навигация
        binding.toolbar.toWrappersBtn.setOnClickListener {
            appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
        }

        // Ссылки
        binding.linkVideo.setOnClickListener { openLink(linerFav.video) }
        binding.linkVk.setOnClickListener { openLink(linerFav.vkArticle) }
        binding.linkWiki.setOnClickListener { openLink(linerFav.wikiArticle) }
        binding.websiteSociete.setOnClickListener { openLink(linerFav.websiteSociete) }

        // Сохранение заметки
        binding.saveNoteBtn.setOnClickListener {
            val note = binding.noteInput.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                appDao.editNoteLiner(linerFav.uniqueNumber, note)
                withContext(Dispatchers.Main) {
                    binding.noteTxtView.text = note
                }
            }
        }

        // Увеличение изображения по центру экрана с затемнением фона
        binding.imageView.setOnClickListener {
            if (linerFav.imageUrlLiner.isNotEmpty()) {
                showZoomedImage(linerFav.imageUrlLiner)
            }
        }
    }

    private fun openLink(url: String) {
        if (url != "-") {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun loadNoteFromDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            addedNote = appDao.getNoteLiner(linerFav.uniqueNumber)
            withContext(Dispatchers.Main) {
                if (addedNote != "-") {
                    binding.noteTxtView.text = addedNote
                } else addedNote = "Заметка отсутствует"
            }
        }
    }

    private fun showZoomedImage(imageUrl: String) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val view = layoutInflater.inflate(R.layout.dialog_zoom_image, null)
        val imageView = view.findViewById<ImageView>(R.id.zoomImageView)

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        dialog.setContentView(view)
        dialog.setCancelable(true)

        // Закрытие по клику на фон или на картинку
        view.setOnClickListener { dialog.dismiss() }
        imageView.setOnClickListener { dialog.dismiss() }

        dialog.show()
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