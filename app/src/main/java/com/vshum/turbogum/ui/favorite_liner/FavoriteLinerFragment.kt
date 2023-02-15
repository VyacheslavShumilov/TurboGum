package com.vshum.turbogum.ui.favorite_liner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentFavoriteLinerBinding
import com.vshum.turbogum.model.LinersFavourite


class FavoriteLinerFragment(var linerFav: LinersFavourite) : Fragment() {
    private lateinit var binding: FragmentFavoriteLinerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteLinerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initIcons()
        initTextViews()

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
            binding.noteTxtView.text = note
        }
    }

    private fun initIcons() {
        if (linerFav.video == "-") binding.linkVideo.visibility = View.GONE
        if (linerFav.vkArticle == "-") binding.linkVk.visibility = View.GONE
        if (linerFav.wikiArticle == "-") binding.linkWiki.visibility = View.GONE
        if (linerFav.imageUrlLiner.isEmpty()) {
            binding.imageView.setImageResource(R.drawable.placeholder2)
        } else {
            Picasso.get().load(linerFav.imageUrlLiner).into(binding.imageView)
        }
    }

    private fun initTextViews() {
        with(binding) {
            indexTxtView.text = linerFav.index
            seriesTxtView.text = linerFav.series
            numberLinerTxtView.text = linerFav.numberLiner
        }
    }

}