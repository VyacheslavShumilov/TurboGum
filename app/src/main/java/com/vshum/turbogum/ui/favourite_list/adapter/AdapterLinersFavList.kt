package com.vshum.turbogum.ui.favourite_list.adapter

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
        fun bindView(item: LinersFavourite) {
            with(binding) {
                // Загружаем изображение
                if (item.imageUrlLiner.isNotEmpty()) {
                    Picasso.get()
                        .load(item.imageUrlLiner)
                        .into(linerImageView)
                }

                // Бейдж серии
                indexSeries.text = item.index

                // Кнопка удаления
                imageBtnDelete.setOnClickListener {
                    listener.onDeleteFavorite(item)
                    deleteItem(item)
                    notifyDataSetChanged()
                }

                // Клик по карточке
                root.setOnClickListener {
                    listener.onClickLinerFavorite(item)
                }
            }
        }

        private fun deleteItem(item: LinersFavourite) {
            linersFavourite.remove(item)
            if (linersFavourite.isEmpty()) {
                listener.notFavorite()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemLinerFavouriteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
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