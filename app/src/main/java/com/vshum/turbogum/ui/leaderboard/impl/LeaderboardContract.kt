package com.vshum.turbogum.ui.leaderboard.impl

import com.vshum.turbogum.model.UserProfile

interface LeaderboardContract {

    interface View {
        fun onUsers(users: List<UserProfile>, field: String)
    }

    interface Presenter {
        /** Subscribes to the leaderboard for [field] (one of [com.vshum.turbogum.model.SeriesStats.TOTAL_PERCENT_KEY] or series keys). */
        fun selectFilter(field: String)
        fun currentUid(): String?
        fun onDestroy()
    }
}
