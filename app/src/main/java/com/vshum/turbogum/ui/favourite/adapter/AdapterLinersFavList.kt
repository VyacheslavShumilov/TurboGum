package com.vshum.turbogum.ui.favourite.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vshum.turbogum.databinding.ItemLinerFavouriteBinding
import com.vshum.turbogum.model.LinersFavourite

class AdapterLinersFavList(
    private val linersFavourite: ArrayList<LinersFavourite>,
    private val listener: OnClickListener
) : RecyclerView.Adapter<AdapterLinersFavList.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLinerFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView(linersFavourite: LinersFavourite) {
            with(binding) {
                Picasso.get().load(linersFavourite.imageUrlLiner).into(linerImageView)
                indexSeries.text = linersFavourite.index
//                linerBrandTxtView.text = linersFavourite.brand

                imageBtnDelete.setOnClickListener {
                    listener.onDeleteFavorite(linersFavourite)
                    notifyDataSetChanged()
                    deleteItem(linersFavourite)
                    notifyDataSetChanged()
                }
            }
        }

        private fun deleteItem(linersFav: LinersFavourite) {
            linersFavourite.remove(linersFav)
            if (linersFavourite.size == 0) {
                listener.notFavorite()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLinerFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(linersFavourite[position])
    }

    override fun getItemCount(): Int = linersFavourite.size

    interface OnClickListener {
        fun onDeleteFavorite(linersFav: LinersFavourite)
        fun notFavorite()
    }
}