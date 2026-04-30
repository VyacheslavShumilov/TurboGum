package com.vshum.turbogum.ui.liners_lists.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import com.vshum.turbogum.R
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.ItemLinerBinding
import com.vshum.turbogum.model.Liner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Adapter for the sticker grid on the SeriesList screen.
 * Each item: image with rarity badge + fav button + brand/model/number.
 */
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
            // ── Brand / model / number ──────────────────────────────
            binding.linerBrand.text = liner.brand
            binding.linerModel.text = liner.model
            binding.linerNumber.text = "#${liner.numberLiner}"

            // ── Rarity badge ────────────────────────────────────────
            applyRarity(liner)

            // ── Image with shimmer ──────────────────────────────────
            val imageView = binding.linerImageView
            val shimmer = binding.shimmerLayout

            shimmer.startShimmer()
            shimmer.visibility = View.VISIBLE
            imageView.visibility = View.GONE

            if (liner.imageUrlLiner.isNotEmpty()) {
                Picasso.get()
                    .load(liner.imageUrlLiner)
                    .placeholder(android.R.color.transparent)
                    .error(android.R.color.transparent)
                    .into(imageView, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            shimmer.stopShimmer()
                            shimmer.visibility = View.GONE
                            imageView.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception?) {
                            shimmer.stopShimmer()
                            shimmer.visibility = View.GONE
                        }
                    })
            } else {
                shimmer.stopShimmer()
                shimmer.visibility = View.GONE
            }

            // ── Favourite state ─────────────────────────────────────
            CoroutineScope(Dispatchers.IO).launch {
                val isFav = appDao.getLinerFavorite(liner.uniqueNumber) != null
                withContext(Dispatchers.Main) {
                    binding.btnFavourite.setImageResource(
                        if (isFav) R.drawable.ic_favorite_filled
                        else R.drawable.ic_favorite_border
                    )
                }
            }

            // ── Click ───────────────────────────────────────────────
            binding.root.setOnClickListener { listener.onClickLiner(liner) }
        }

        private fun applyRarity(liner: Liner) {
            // Rarity is derived from sticker number ranges per spec:
            // 1-50 common, 51-120 uncommon, 121-190 rare, 191+ ultra-rare
            val num = liner.numberLiner.toIntOrNull() ?: 0
            val (bg, label) = when {
                num <= 50 -> R.drawable.badge_rarity_common to "Common"
                num <= 120 -> R.drawable.badge_rarity_uncommon to "Uncommon"
                num <= 190 -> R.drawable.badge_rarity_rare to "Rare"
                else -> R.drawable.badge_rarity_ultra to "Ultra"
            }
            binding.rarityBadge.setBackgroundResource(bg)
            binding.rarityBadge.text = label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemLinerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(linersList[position])
    }

    override fun getItemCount(): Int = linersList.size
}