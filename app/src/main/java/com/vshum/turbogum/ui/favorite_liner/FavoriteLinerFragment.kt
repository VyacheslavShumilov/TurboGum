package com.vshum.turbogum.ui.favorite_liner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentFavoriteLinerBinding
import com.vshum.turbogum.model.LinersFavourite


class FavoriteLinerFragment(linerFav: LinersFavourite) : Fragment() {
    private lateinit var binding: FragmentFavoriteLinerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteLinerBinding.inflate(inflater, container, false)
        return binding.root
    }

}