package com.vshum.turbogum.ui.liners_lists.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vshum.turbogum.R
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.ItemLinerBinding
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.model.LinersFavourite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterLinersList(
    private var linersList: ArrayList<Liner>,
    private val listener: SetOnClickListener,
    private val appDao: LinersDao
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

            /***
             * Запрос к базе данных getLinerFavorite выполняется асинхронно с помощью CoroutineScope(Dispatchers.IO).launch,
             * значит, foundInDb устанавливается на значение по умолчанию, которое равно false, до того, как запрос будет выполнен.
             * Для исправления этой ошибки вам необходимо использовать сопрограммы и suspend функции для получения результата запроса перед установкой значения foundInDb
             */

            CoroutineScope(Dispatchers.IO).launch {
                val favLiner = appDao.getLinerFavorite(liner.uniqueNumber)
                val foundInDb = favLiner != null
                withContext(Dispatchers.Main) {
                    if (foundInDb) {
                        addedToCollection.visibility = View.VISIBLE
                        //выделяются рамкой не выбранные вкладыши
                        //card.strokeColor = ContextCompat.getColor(holder.itemView.context, R.color.card_stroke_color)
                    } else {
                        addedToCollection.visibility = View.GONE
                    }
                }
            }

            holder.bindView(liner)
        }
    }


    override fun getItemCount(): Int = linersList.size

    interface SetOnClickListener {
        fun onClickLiner(liner: Liner)
    }
}



