package com.vshum.turbogum.ui.liners_lists.turbo.adapter

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
        fun bindView(linersList: Liner) {
            itemView.setOnClickListener {
                listener.onClickLiner(linersList.id)
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
            linerNumberTxtView.text = liner.numberLiner.toString()
            holder.bindView(liner)
        }
    }

    override fun getItemCount(): Int = linersList.size

    interface SetOnClickListener {
        fun onClickLiner(idLiner: String)
    }
}



