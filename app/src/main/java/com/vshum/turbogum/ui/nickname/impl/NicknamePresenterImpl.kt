package com.vshum.turbogum.ui.nickname.impl

import com.vshum.turbogum.data.AuthRepository
import com.vshum.turbogum.data.UserRepository

class NicknamePresenterImpl(
    private val view: NicknameContract.View,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : NicknameContract.Presenter {

    override fun prefilledNickname(): String =
        authRepository.currentUser?.displayName.orEmpty()

    override fun saveNickname(nickname: String) {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            view.onError(NOT_AUTHENTICATED_ERROR)
            return
        }
        if (nickname.isBlank()) {
            view.onError(EMPTY_NICKNAME_ERROR)
            return
        }
        view.setLoading(true)
        val onFail = { e: Exception ->
            view.setLoading(false)
            view.onError(e.localizedMessage ?: e.toString())
        }
        userRepository.createUserIfMissing(
            uid, nickname,
            onComplete = {
                userRepository.updateNickname(
                    uid, nickname,
                    onComplete = {
                        view.setLoading(false)
                        view.onSaved()
                    },
                    onError = onFail
                )
            },
            onError = onFail
        )
    }

    companion object {
        const val EMPTY_NICKNAME_ERROR = "EMPTY_NICKNAME"
        const val NOT_AUTHENTICATED_ERROR = "NOT_AUTHENTICATED"
    }
}
