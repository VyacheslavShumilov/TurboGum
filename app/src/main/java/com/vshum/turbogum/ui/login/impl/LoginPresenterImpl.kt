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
        val message = when {
            e.message?.contains("password is invalid") == true ||
            e.message?.contains("INVALID_PASSWORD") == true ->
                "Неверный пароль"

            e.message?.contains("no user record") == true ||
            e.message?.contains("USER_NOT_FOUND") == true ||
            e.message?.contains("There is no user record") == true ->
                "Пользователь с таким email не найден"

            e.message?.contains("email address is badly formatted") == true ||
            e.message?.contains("INVALID_EMAIL") == true ->
                "Неверный формат email"

            e.message?.contains("email address is already in use") == true ||
            e.message?.contains("EMAIL_EXISTS") == true ->
                "Этот email уже зарегистрирован"

            e.message?.contains("password should be at least 6") == true ||
            e.message?.contains("WEAK_PASSWORD") == true ->
                "Пароль должен содержать не менее 6 символов"

            e.message?.contains("network error") == true ||
            e.message?.contains("NETWORK_ERROR") == true ->
                "Ошибка сети. Проверьте подключение к интернету"

            e.message?.contains("too many requests") == true ||
            e.message?.contains("TOO_MANY_ATTEMPTS") == true ->
                "Слишком много попыток. Попробуйте позже"

            e.message?.contains("user has been disabled") == true ||
            e.message?.contains("USER_DISABLED") == true ->
                "Аккаунт заблокирован"

            else -> e.localizedMessage ?: "Произошла ошибка. Попробуйте ещё раз"
        }
        view.onAuthError(message)
    }

    companion object {
        const val EMPTY_FIELDS_ERROR = "EMPTY_FIELDS"
    }
}
