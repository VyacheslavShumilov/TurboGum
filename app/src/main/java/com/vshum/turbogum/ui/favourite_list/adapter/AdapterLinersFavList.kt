package com.vshum.turbogum.ui.favourite_list.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vshum.turbogum.databinding.ItemLinerFavouriteBinding
import com.vshum.turbogum.model.LinersFavourite

/**
 * Adapter for the Favourites screen.
 * Each item: 88×88 thumbnail with series badge + brand/model/series·number
 * + quick links row (VK/YT/Wiki/Société) + delete button.
 */
class AdapterLinersFavList(
    private val linersFavourite: ArrayList<LinersFavourite>,
    private val listener: OnClickListener
) : RecyclerView.Adapter<AdapterLinersFavList.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLinerFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bindView(item: LinersFavourite) {
            with(binding) {
                // Thumbnail
                if (item.imageUrlLiner.isNotEmpty()) {
                    Picasso.get().load(item.imageUrlLiner).into(linerImageView)
                }

                // Text
                favBrand.text = item.brand
                favModel.text = item.model
                favSeries.text = item.series
                favNumber.text = "#${item.numberLiner}"

                // Quick links — open URL in browser
                quickVk.visibility = if (item.vkArticle == "-") View.GONE else View.VISIBLE
                quickVk.setOnClickListener { openUrl(item.vkArticle) }

                quickVideo.visibility = if (item.video == "-") View.GONE else View.VISIBLE
                quickVideo.setOnClickListener { openUrl(item.video) }

                quickWiki.visibility = if (item.wikiArticle == "-") View.GONE else View.VISIBLE
                quickWiki.setOnClickListener { openUrl(item.wikiArticle) }

                quickSociete.visibility = if (item.websiteSociete == "-") View.GONE else View.VISIBLE
                quickSociete.setOnClickListener { openUrl(item.websiteSociete) }

                // Delete
                imageBtnDelete.setOnClickListener {
                    listener.onDeleteFavorite(item)
                    deleteItem(item)
                    notifyDataSetChanged()
                }

                // Card click → open detail
                root.setOnClickListener { listener.onClickLinerFavorite(item) }
            }
        }

        private fun deleteItem(item: LinersFavourite) {
            linersFavourite.remove(item)
            if (linersFavourite.isEmpty()) listener.notFavorite()
        }

        private fun openUrl(url: String) {
            if (url.isNotBlank() && url != "-") {
                val ctx = binding.root.context
                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemLinerFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(linersFavourite[position])
    }

    override fun getItemCount(): Int = linersFavourite.size

    interface OnClickListener {
        fun onDeleteFavorite(linersFav: LinersFavourite)
        fun notFavorite()
        fun onClickLinerFavorite(linersFav: LinersFavourite)
    }

    fun updateData(newList: ArrayList<LinersFavourite>) {
        linersFavourite.clear()
        linersFavourite.addAll(newList)
        notifyDataSetChanged()
    }
}