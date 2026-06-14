package com.vshum.turbogum.ui.favourite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vshum.turbogum.databinding.ItemLinerFavouriteBinding
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.model.LinersFavourite

class AdapterLinersFavList(
    private val allLiners: List<Liner>,
    private val ownedMap: Map<String, LinersFavourite>,
    private val listener: OnClickListener
) : RecyclerView.Adapter<AdapterLinersFavList.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLinerFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(liner: Liner) {
            val owned = ownedMap[liner.uniqueNumber]
            binding.numberText.text = liner.numberLiner

            if (owned != null) {
                // owned: показываем картинку, номер в левом верхнем углу
                binding.linerImageView.visibility = View.VISIBLE
                binding.placeholderBg.visibility = View.GONE
                val params = binding.numberText.layoutParams as ViewGroup.LayoutParams
                (binding.numberText.layoutParams as? android.widget.FrameLayout.LayoutParams)?.apply {
                    gravity = android.view.Gravity.TOP or android.view.Gravity.START
                }
                binding.numberText.setTextColor(android.graphics.Color.WHITE)
                binding.numberText.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                Picasso.get()
                    .load(liner.imageUrlLiner)
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.darker_gray)
                    .fit()
                    .centerCrop()
                    .into(binding.linerImageView)
                binding.root.setOnClickListener { listener.onClickLinerFavorite(owned) }
            } else {
                // not owned: серый фон, номер по центру
                binding.linerImageView.visibility = View.GONE
                binding.placeholderBg.visibility = View.VISIBLE
                (binding.numberText.layoutParams as? android.widget.FrameLayout.LayoutParams)?.apply {
                    gravity = android.view.Gravity.CENTER
                }
                binding.numberText.setTextColor(android.graphics.Color.parseColor("#666666"))
                binding.numberText.setShadowLayer(0f, 0f, 0f, android.graphics.Color.TRANSPARENT)
                binding.root.setOnClickListener(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLinerFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val screenWidth = parent.context.resources.displayMetrics.widthPixels
        val spanCount = ((parent as? RecyclerView)?.layoutManager as? GridLayoutManager)?.spanCount ?: 3
        val itemWidth = screenWidth / spanCount
        val itemHeight = itemWidth * 2 / 3 // соотношение 3:2 (шире чем выше)
        binding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            itemHeight
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(allLiners[position])
    override fun getItemCount() = allLiners.size

    interface OnClickListener {
        fun onClickLinerFavorite(linersFav: LinersFavourite)
    }
}
