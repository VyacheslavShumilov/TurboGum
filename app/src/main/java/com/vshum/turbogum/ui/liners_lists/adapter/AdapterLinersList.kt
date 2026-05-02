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
 * Each item: image with shimmer + rarity badge + fav button + brand/model/number.
 *
 * Rarity is derived from the sticker's global number:
 *   Серия 1–5 / Super 1–3 use a global numbering 1..540
 *   Sport / Classic restart from 1 — we scale rarity relative to series size
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
            binding.linerBrand.text  = liner.brand
            binding.linerModel.text  = liner.model
            binding.linerNumber.text = "#${liner.numberLiner}"

            // ── Rarity badge ────────────────────────────────────────
            applyRarity(liner)

            // ── Image + shimmer ─────────────────────────────────────
            val imageView = binding.linerImageView
            val shimmer   = binding.shimmerLayout

            shimmer.startShimmer()
            shimmer.visibility  = View.VISIBLE
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
                            shimmer.visibility  = View.GONE
                            imageView.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception?) {
                            Log.e("Picasso", "Failed to load image: $url", e)
                            shimmer.stopShimmer()
                            shimmer.visibility  = View.GONE
                            imageView.visibility = View.VISIBLE
                            // placeholder already applied by Picasso .error()
                        }
                    })
            } else {
                shimmer.stopShimmer()
                shimmer.visibility  = View.GONE
                imageView.visibility = View.VISIBLE
                imageView.setImageResource(R.drawable.placeholder)
            }

            // ── Favourite state ─────────────────────────────────────
            CoroutineScope(Dispatchers.IO).launch {
                val isFav = try {
                    appDao.getLinerFavorite(liner.uniqueNumber) != null
                } catch (e: Exception) {
                    false
                }
                withContext(Dispatchers.Main) {
                    binding.btnFavourite.setImageResource(
                        if (isFav) R.drawable.ic_favorite_filled
                        else       R.drawable.ic_favorite_border
                    )
                }
            }

            // ── Click ───────────────────────────────────────────────
            binding.root.setOnClickListener { listener.onClickLiner(liner) }
        }

        /**
         * Assign rarity badge based on series type + sticker number.
         *
         * "Серия 1–5" and "Super 1–3" have a continuous global numbering:
         *   Серия 1:  1–50   → common
         *   Серия 2:  51–120 → uncommon
         *   Серия 3: 121–190 → rare
         *   Серия 4: 191–260 → ultra
         *   Серия 5: 261–330 → ultra
         *   Super 1–3: 331–540 → ultra
         *
         * "Sport" and "Classic" restart from 1. We treat them relative to
         * quarter boundaries within each 70-card series:
         *   1–18  common  (≈25 %)
         *  19–35  uncommon (≈25 %)
         *  36–52  rare     (≈25 %)
         *  53–70  ultra    (≈25 %)
         */
        private fun applyRarity(liner: Liner) {
            val num    = liner.numberLiner.toIntOrNull() ?: 0
            val series = liner.series.trim()

            val (bg, label) = when {
                series.startsWith("Sport") || series.startsWith("Classic") -> {
                    // relative quartile within 70-card series
                    when {
                        num <= 18 -> R.drawable.badge_rarity_common   to "Common"
                        num <= 35 -> R.drawable.badge_rarity_uncommon to "Uncommon"
                        num <= 52 -> R.drawable.badge_rarity_rare     to "Rare"
                        else      -> R.drawable.badge_rarity_ultra    to "Ultra"
                    }
                }
                else -> {
                    // global numbering 1..540
                    when {
                        num <= 50  -> R.drawable.badge_rarity_common   to "Common"
                        num <= 120 -> R.drawable.badge_rarity_uncommon to "Uncommon"
                        num <= 190 -> R.drawable.badge_rarity_rare     to "Rare"
                        else       -> R.drawable.badge_rarity_ultra    to "Ultra"
                    }
                }
            }

            binding.rarityBadge.setBackgroundResource(bg)
            binding.rarityBadge.text = label
        }
    }

    // ── RecyclerView.Adapter ──────────────────────────────────────────

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemLinerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(linersList[position])

    override fun getItemCount(): Int = linersList.size
}