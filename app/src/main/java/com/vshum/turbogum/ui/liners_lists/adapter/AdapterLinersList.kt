package com.vshum.turbogum.ui.liners_lists.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
            // ── Текст ─────────────────────────────────────────────────
            binding.linerBrand.text  = liner.brand
            binding.linerModel.text  = liner.model
            binding.linerNumber.text = "#${liner.numberLiner}"
            binding.seriesBadge.text = liner.index

            // ── Shimmer → Image ───────────────────────────────────────
            val shimmer = binding.shimmerLayout
            val imageView = binding.linerImageView

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

            // ── Favourite icon ────────────────────────────────────────
            CoroutineScope(Dispatchers.IO).launch {
                val isFav = appDao.getLinerFavorite(liner.uniqueNumber) != null
                withContext(Dispatchers.Main) {
                    binding.btnFavourite.setImageResource(
                        if (isFav) R.drawable.btn_fav else R.drawable.ic_favorite_border
                    )
                    val tint = if (isFav) R.color.brand_purple else R.color.text_hint
                    binding.btnFavourite.setColorFilter(
                        ContextCompat.getColor(binding.root.context, tint)
                    )
                }
            }

            // ── Click ─────────────────────────────────────────────────
            binding.root.setOnClickListener { listener.onClickLiner(liner) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemLinerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(linersList[position])
    }

    override fun getItemCount(): Int = linersList.size
}