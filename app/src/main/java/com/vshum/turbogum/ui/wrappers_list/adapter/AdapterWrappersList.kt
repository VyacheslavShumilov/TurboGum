package com.vshum.turbogum.ui.wrappers_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vshum.turbogum.databinding.CardWrapperBinding
import com.vshum.turbogum.model.WrappersList

class AdapterWrappersList(
    private var wrappersList: ArrayList<WrappersList>,
    private val listener: AdapterWrappersList.SetOnClickListener
) : RecyclerView.Adapter<AdapterWrappersList.ViewHolder>() {
    inner class ViewHolder(var binding: CardWrapperBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(wrappersList: WrappersList) {
            itemView.setOnClickListener {
                listener.onClickLiner(wrappersList.nameWrapper)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardWrapperBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wrappers = wrappersList[position]
        with(holder.binding) {
            Picasso.get().load(wrappers.imageUrlWrapper).into(wrapperImageView)
            wrapperNameTxtView.text = wrappers.nameWrapper
            holder.bindView(wrappers)
        }
    }

    override fun getItemCount(): Int = wrappersList.size

    interface SetOnClickListener {
        fun onClickLiner(nameWrapper: String)
    }
}



