package com.vshum.turbogum.ui.login.impl

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.vshum.turbogum.data.AuthRepository
import com.vshum.turbogum.data.UserRepository

class LoginPresenterImpl(
    private val view: LoginContract.View,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : LoginContract.Presenter {

    override fun signInEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            view.onAuthError(EMPTY_FIELDS_ERROR)
            return
        }
        view.setLoading(true)
        authRepository.signInWithEmail(
            email, password,
            onSuccess = { user -> checkNickname(user) },
            onError = { e -> handleError(e) }
        )
    }

    override fun registerEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            view.onAuthError(EMPTY_FIELDS_ERROR)
            return
        }
        view.setLoading(true)
        authRepository.registerWithEmail(
            email, password,
            onSuccess = { user -> checkNickname(user) },
            onError = { e -> handleError(e) }
        )
    }

    override fun onGoogleAccount(account: GoogleSignInAccount) {
        view.setLoading(true)
        authRepository.signInWithGoogleAccount(
            account,
            onSuccess = { user -> checkNickname(user) },
            onError = { e -> handleError(e) }
        )
    }

    private fun checkNickname(user: FirebaseUser) {
        userRepository.getUser(
            user.uid,
            onResult = { profile ->
                view.setLoading(false)
                view.onAuthSuccess(profile == null || profile.nickname.isBlank())
            },
            onError = {
                view.setLoading(false)
                view.onAuthSuccess(true)
            }
        )
    }

    private fun handleError(e: Exception) {
        view.setLoading(false)
        view.onAuthError(e.localizedMessage ?: e.toString())
    }

    companion object {
        const val EMPTY_FIELDS_ERROR = "EMPTY_FIELDS"
    }
}
