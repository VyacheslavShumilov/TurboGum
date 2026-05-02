package com.vshum.turbogum.ui.liners_lists.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

/**
 * Adapter for the sticker grid on the SeriesList screen.
 *
 * Each item: shimmer placeholder → sticker image + small rarity dot
 * (no text label) + fav button + brand (uppercase) / model / number.
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

            // ── Text fields ─────────────────────────────────────────
            binding.linerBrand.text  = liner.brand.uppercase()
            binding.linerModel.text  = liner.model
            binding.linerNumber.text = "#${liner.numberLiner}"

            // ── Rarity dot colour ───────────────────────────────────
            applyRarity(liner)

            // ── Shimmer + image ─────────────────────────────────────
            val imageView = binding.linerImageView
            val shimmer   = binding.shimmerLayout

            shimmer.startShimmer()
            shimmer.visibility   = View.VISIBLE
            imageView.visibility = View.GONE

            val url = liner.imageUrlLiner.trim()
            if (url.isNotEmpty()) {
                Picasso.get()
                    .load(url)
                    .placeholder(android.R.color.transparent)
                    .error(R.drawable.placeholder)
                    .into(imageView, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            shimmer.stopShimmer()
                            shimmer.visibility   = View.GONE
                            imageView.visibility = View.VISIBLE
                        }
                        override fun onError(e: Exception?) {
                            Log.e("Picasso", "Failed: $url", e)
                            shimmer.stopShimmer()
                            shimmer.visibility   = View.GONE
                            imageView.visibility = View.VISIBLE
                        }
                    })
            } else {
                shimmer.stopShimmer()
                shimmer.visibility   = View.GONE
                imageView.visibility = View.VISIBLE
                imageView.setImageResource(R.drawable.placeholder)
            }

            // ── Favourite state ─────────────────────────────────────
            CoroutineScope(Dispatchers.IO).launch {
                val isFav = try {
                    appDao.getLinerFavorite(liner.uniqueNumber) != null
                } catch (e: Exception) { false }
                withContext(Dispatchers.Main) {
                    binding.btnFavourite.setImageResource(
                        if (isFav) R.drawable.ic_favorite_filled
                        else       R.drawable.ic_favorite_border
                    )
                }
            }

            binding.root.setOnClickListener { listener.onClickLiner(liner) }
        }

        /**
         * rarityBadge is now a plain View (coloured dot) — we only set background.
         * Colour rules:
         *   Серия 1–5 / Super 1–3: global numbering 1..540
         *   Sport / Classic: relative to 70-card series
         */
        private fun applyRarity(liner: Liner) {
            val num    = liner.numberLiner.toIntOrNull() ?: 0
            val series = liner.series.trim()

            val bg = when {
                series.startsWith("Sport") || series.startsWith("Classic") -> when {
                    num <= 18 -> R.drawable.badge_rarity_common
                    num <= 35 -> R.drawable.badge_rarity_uncommon
                    num <= 52 -> R.drawable.badge_rarity_rare
                    else      -> R.drawable.badge_rarity_ultra
                }
                else -> when {
                    num <= 50  -> R.drawable.badge_rarity_common
                    num <= 120 -> R.drawable.badge_rarity_uncommon
                    num <= 190 -> R.drawable.badge_rarity_rare
                    else       -> R.drawable.badge_rarity_ultra
                }
            }
            binding.rarityBadge.setBackgroundResource(bg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemLinerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(linersList[position])

    override fun getItemCount(): Int = linersList.size
}