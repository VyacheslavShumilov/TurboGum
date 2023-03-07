package com.vshum.turbogum.ui.liners_lists.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vshum.turbogum.databinding.ItemLinerBinding
import com.vshum.turbogum.model.Liner

class AdapterLinersList(
    private var linersList: ArrayList<Liner>,
    private val listener: SetOnClickListener
) : RecyclerView.Adapter<AdapterLinersList.ViewHolder>() {

    inner class ViewHolder(var binding: ItemLinerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(liner: Liner) {
            itemView.setOnClickListener {
                listener.onClickLiner(liner)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLinerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val liner = linersList[position]
        with(holder.binding) {
            Picasso.get().load(liner.imageUrlLiner).into(linerImageView)
            holder.bindView(liner)
        }
    }

    override fun getItemCount(): Int = linersList.size

    interface SetOnClickListener {
        fun onClickLiner(liner: Liner)
    }
}



