package com.vshum.turbogum.ui.liner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import com.vshum.turbogum.databinding.FragmentLinerBinding
import com.vshum.turbogum.model.Liner


class LinerFragment(var liner: Liner) : Fragment() {
    private lateinit var binding: FragmentLinerBinding

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


        binding.linkVideo.setOnClickListener {
            val uri: Uri = Uri.parse(liner.video)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding.linkVk.setOnClickListener {
            val uri: Uri = Uri.parse(liner.vkArticle)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        binding.linkWiki.setOnClickListener {
            val uri: Uri = Uri.parse(liner.wikiArticle)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }


    }


}