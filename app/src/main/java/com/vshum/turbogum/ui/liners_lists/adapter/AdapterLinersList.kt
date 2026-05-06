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
    private val fullList: List<Liner>,
    private val listener: SetOnClickListener,
    private val appDao: LinersDao
) : RecyclerView.Adapter<AdapterLinersList.ItemViewHolder>() {

    interface SetOnClickListener {
        fun onClickLiner(liner: Liner)
    }

    override fun getItemCount(): Int = fullList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemLinerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(fullList[position])
    }

    inner class ItemViewHolder(
        val binding: ItemLinerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(liner: Liner) {

            binding.linerBrand.text = liner.brand.uppercase()
            binding.linerModel.text = liner.model
            binding.linerNumber.text = "#${liner.numberLiner}"

            loadImage(liner)
            loadFavState(liner)

            binding.root.setOnClickListener {
                listener.onClickLiner(liner)
            }
        }

        private fun loadImage(liner: Liner) {
            val shimmer = binding.shimmerLayout
            val image = binding.linerImageView

            shimmer.startShimmer()
            shimmer.visibility = View.VISIBLE
            image.visibility = View.GONE

            Picasso.get()
                .load(liner.imageUrlLiner)
                .placeholder(android.R.color.transparent)
                .error(R.drawable.placeholder)
                .into(image, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        shimmer.stopShimmer()
                        shimmer.visibility = View.GONE
                        image.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        shimmer.stopShimmer()
                        shimmer.visibility = View.GONE
                        image.visibility = View.VISIBLE
                    }
                })
        }

        private fun loadFavState(liner: Liner) {
            CoroutineScope(Dispatchers.IO).launch {
                val isFav = try {
                    appDao.getLinerFavorite(liner.uniqueNumber) != null
                } catch (e: Exception) {
                    false
                }

                withContext(Dispatchers.Main) {
                    binding.btnFavourite.setImageResource(
                        if (isFav) R.drawable.ic_favorite_filled
                        else R.drawable.ic_favorite_border
                    )
                }
            }
        }
    }
}