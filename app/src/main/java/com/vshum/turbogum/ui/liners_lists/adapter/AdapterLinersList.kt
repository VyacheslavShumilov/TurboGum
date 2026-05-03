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
 * Adapter for the sticker grid with pagination.
 *
 * Only [pageSize] items are shown initially; each time the user scrolls
 * near the bottom, [loadNextPage] adds the next batch. This limits the
 * number of simultaneous Picasso requests to ~pageSize instead of all 70.
 *
 * Two view types:
 *   VIEW_TYPE_ITEM    — normal sticker card
 *   VIEW_TYPE_LOADING — invisible footer that triggers next page load
 */
class AdapterLinersList(
    private val fullList: List<Liner>,       // full filtered list, supplied once
    private val listener: SetOnClickListener,
    private val appDao: LinersDao,
    private val pageSize: Int = 20
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface SetOnClickListener {
        fun onClickLiner(liner: Liner)
    }

    companion object {
        private const val VIEW_TYPE_ITEM    = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    // Visible slice of fullList
    private val visibleList = ArrayList<Liner>()
    private var isLoading = false

    init {
        // Load first page immediately
        val first = fullList.take(pageSize)
        visibleList.addAll(first)
    }

    // ── Public API ────────────────────────────────────────────────────

    /** Call when user scrolls near the bottom. Appends the next page. */
    fun loadNextPage() {
        if (isLoading) return
        val loaded = visibleList.size
        if (loaded >= fullList.size) return   // nothing more to load

        isLoading = true
        notifyItemChanged(itemCount - 1)      // refresh loading footer

        val next = fullList.subList(loaded, minOf(loaded + pageSize, fullList.size))
        val insertStart = visibleList.size
        visibleList.addAll(next)
        notifyItemRangeInserted(insertStart, next.size)

        isLoading = false
        notifyItemChanged(itemCount - 1)
    }

    val hasMore: Boolean get() = visibleList.size < fullList.size

    // ── RecyclerView.Adapter ──────────────────────────────────────────

    override fun getItemViewType(position: Int): Int =
        if (position < visibleList.size) VIEW_TYPE_ITEM else VIEW_TYPE_LOADING

    override fun getItemCount(): Int =
        visibleList.size + if (hasMore) 1 else 0   // +1 for loading footer

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            ItemViewHolder(
                ItemLinerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            // Invisible footer view — just triggers pagination
            val v = View(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(0, 0)
            }
            LoadingViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) holder.bind(visibleList[position])
    }

    // ── ViewHolders ───────────────────────────────────────────────────

    inner class ItemViewHolder(val binding: ItemLinerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(liner: Liner) {
            binding.linerBrand.text  = liner.brand.uppercase()
            binding.linerModel.text  = liner.model
            binding.linerNumber.text = "#${liner.numberLiner}"

            applyRarity(liner)
            loadImage(liner)
            loadFavState(liner)

            binding.root.setOnClickListener { listener.onClickLiner(liner) }
        }

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

        private fun loadImage(liner: Liner) {
            val shimmer   = binding.shimmerLayout
            val imageView = binding.linerImageView

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
                imageView.setImageResource(R.drawable.placeholder)
                imageView.visibility = View.VISIBLE
            }
        }

        private fun loadFavState(liner: Liner) {
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
        }
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}