package com.vshum.turbogum.ui.leaderboard.impl

import com.google.firebase.firestore.ListenerRegistration
import com.vshum.turbogum.data.AuthRepository
import com.vshum.turbogum.data.UserRepository
import com.vshum.turbogum.model.SeriesStats

class LeaderboardPresenterImpl(
    private val view: LeaderboardContract.View,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : LeaderboardContract.Presenter {

    private var registration: ListenerRegistration? = null

    override fun selectFilter(field: String) {
        registration?.remove()
        registration = userRepository.observeLeaderboard(field) { users ->
            view.onUsers(users, field)
        }
    }

    override fun currentUid(): String? = authRepository.currentUser?.uid

    override fun onDestroy() {
        registration?.remove()
        registration = null
    }

    companion object {
        const val DEFAULT_FIELD = SeriesStats.TOTAL_PERCENT_KEY
    }
}
