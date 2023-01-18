package com.vshum.turbogum.ui.liners_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vshum.turbogum.databinding.CardLinerBinding
import com.vshum.turbogum.model.LinersList

class AdapterLinersList(
    private var linersList: ArrayList<LinersList>,
    private val listener: AdapterLinersList.SetOnClickListener
) : RecyclerView.Adapter<AdapterLinersList.ViewHolder>() {
    inner class ViewHolder(var binding: CardLinerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(linersList: LinersList) {
            itemView.setOnClickListener {
                listener.onClickLiner(linersList.nameLiner)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardLinerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val liners = linersList[position]
        with(holder.binding) {
            Picasso.get().load(liners.imageUrlLiner).into(linerImageView)
            linerNameTxtView.text = liners.nameLiner
            holder.bindView(liners)
        }
    }

    override fun getItemCount(): Int = linersList.size

    interface SetOnClickListener {
        fun onClickLiner(nameLiner: String)
    }
}



