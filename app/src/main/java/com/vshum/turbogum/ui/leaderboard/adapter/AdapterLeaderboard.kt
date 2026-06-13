package com.vshum.turbogum.ui.leaderboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vshum.turbogum.databinding.ItemLeaderboardUserBinding
import com.vshum.turbogum.model.UserProfile
import kotlin.math.roundToInt

/** Adapter for the leaderboard list. Highlights the row belonging to [currentUid]. */
class AdapterLeaderboard(
    private var items: List<UserProfile>,
    private var field: String,
    private val currentUid: String?
) : RecyclerView.Adapter<AdapterLeaderboard.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLeaderboardUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, user: UserProfile) {
            with(binding) {
                rankText.text = "#${position + 1}"
                nicknameText.text = user.nickname.ifBlank { "Коллекционер" }
                percentText.text = "${user.percentFor(field).roundToInt()}%"
                root.setBackgroundResource(
                    if (user.uid == currentUid) com.vshum.turbogum.R.drawable.bg_leaderboard_highlight
                    else com.vshum.turbogum.R.drawable.surface_card_bg
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemLeaderboardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, items[position])
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<UserProfile>, newField: String) {
        items = newItems
        field = newField
        notifyDataSetChanged()
    }
}
