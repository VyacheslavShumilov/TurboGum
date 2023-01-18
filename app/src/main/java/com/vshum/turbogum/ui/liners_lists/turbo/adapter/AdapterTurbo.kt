package com.vshum.turbogum.ui.liners_lists.turbo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vshum.turbogum.databinding.ItemLinerBinding
import com.vshum.turbogum.model.Liner

class AdapterTurbo(
    private var turboList: ArrayList<Liner>,
    private val listener: SetOnClickListener
) : RecyclerView.Adapter<AdapterTurbo.ViewHolder>() {
    inner class ViewHolder(var binding: ItemLinerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(wrappersList: Liner) {
            itemView.setOnClickListener {
                listener.onClickLiner(wrappersList.nameWrapper)
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
        val liner = turboList[position]
        with(holder.binding) {
            Picasso.get().load(liner.imageUrlLiner).into(linerImageView)
            linerNumberTxtView.text = liner.numberLiner.toString()
            holder.bindView(liner)
        }
    }

    override fun getItemCount(): Int = turboList.size

    interface SetOnClickListener {
        fun onClickLiner(idLiner: String)
    }
}



