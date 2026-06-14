package com.vshum.turbogum.ui.favourite_list.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vshum.turbogum.databinding.ItemCollectionHeaderBinding
import com.vshum.turbogum.databinding.ItemCollectionStickerBinding
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.ui.favourite_list.impl.CollectionSection
import com.vshum.turbogum.ui.favourite_list.impl.CollectionSticker

/** Flattens series sections into a single grid: a full-width header followed by its sticker cells. */
class AdapterCollection(
    private var sections: List<CollectionSection>,
    private val listener: OnClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnClickListener {
        fun onClickSticker(favourite: LinersFavourite)
    }

    private sealed class Item {
        data class Header(val title: String) : Item()
        data class Sticker(val sticker: CollectionSticker) : Item()
    }

    private var items: List<Item> = buildItems(sections).also {
        Log.d("FavouriteList", "adapter items: ${it.size}")
    }

    override fun getItemViewType(position: Int) = when (items[position]) {
        is Item.Header -> TYPE_HEADER
        is Item.Sticker -> TYPE_STICKER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(ItemCollectionHeaderBinding.inflate(inflater, parent, false))
        } else {
            StickerViewHolder(ItemCollectionStickerBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is Item.Header -> (holder as HeaderViewHolder).bind(item.title)
            is Item.Sticker -> (holder as StickerViewHolder).bind(item.sticker)
        }
    }

    override fun getItemCount(): Int = items.size

    fun isHeader(position: Int): Boolean = items[position] is Item.Header

    fun update(newSections: List<CollectionSection>) {
        sections = newSections
        items = buildItems(sections)
        Log.d("FavouriteList", "adapter items updated: ${items.size}")
        notifyDataSetChanged()
    }

    private fun buildItems(sections: List<CollectionSection>): List<Item> =
        sections.flatMap { section ->
            listOf(Item.Header(section.title)) + section.stickers.map { Item.Sticker(it) }
        }

    inner class HeaderViewHolder(private val binding: ItemCollectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.sectionTitle.text = title
        }
    }

    inner class StickerViewHolder(private val binding: ItemCollectionStickerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sticker: CollectionSticker) {
            Picasso.get().cancelRequest(binding.stickerImage)
            val favourite = sticker.favourite
            if (favourite != null) {
                binding.stickerNumber.visibility = View.GONE
                Picasso.get().load(favourite.imageUrlLiner).into(binding.stickerImage)
                binding.root.isClickable = true
                binding.root.setOnClickListener { listener.onClickSticker(favourite) }
            } else {
                binding.stickerImage.setImageDrawable(null)
                binding.stickerNumber.visibility = View.VISIBLE
                binding.stickerNumber.text = "#" + sticker.number.toString().padStart(3, '0')
                binding.root.isClickable = false
                binding.root.setOnClickListener(null)
            }
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_STICKER = 1
    }
}
