package com.vshum.turbogum.ui.liners_lists.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vshum.turbogum.R
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.ItemLinerBinding
import com.vshum.turbogum.model.Liner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterLinersList(
    private val linersList: ArrayList<Liner>,
    private val listener: SetOnClickListener,
    private val appDao: LinersDao
) : RecyclerView.Adapter<AdapterLinersList.ViewHolder>() {

    interface SetOnClickListener {
        fun onClickLiner(liner: Liner)
    }

    inner class ViewHolder(val binding: ItemLinerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(liner: Liner) {
            // ── Image ────────────────────────────────────────────────
            if (liner.imageUrlLiner.isNotEmpty()) {
                Picasso.get()
                    .load(liner.imageUrlLiner)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.linerImageView)
            } else {
                binding.linerImageView.setImageResource(R.drawable.placeholder)
            }

            // ── Text ─────────────────────────────────────────────────
            binding.linerBrand.text   = liner.brand
            binding.linerModel.text   = liner.model
            binding.linerNumber.text  = "#${liner.numberLiner}"
            binding.seriesBadge.text  = liner.index

            // ── Favourite icon: async DB check ────────────────────────
            CoroutineScope(Dispatchers.IO).launch {
                val isFav = appDao.getLinerFavorite(liner.uniqueNumber) != null
                withContext(Dispatchers.Main) {
                    binding.btnFavourite.setImageResource(
                        if (isFav) R.drawable.btn_fav else R.drawable.ic_favorite_border
                    )
                    val tintColor = if (isFav) R.color.brand_purple else R.color.text_hint
                    binding.btnFavourite.setColorFilter(
                        ContextCompat.getColor(binding.root.context, tintColor)
                    )
                }
            }

            // ── Progress bar (placeholder at 0 until real stats ready) ─
            binding.collectionProgress.progress = 0

            // ── Click ─────────────────────────────────────────────────
            binding.root.setOnClickListener { listener.onClickLiner(liner) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLinerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(linersList[position])
    }

    override fun getItemCount(): Int = linersList.size
}