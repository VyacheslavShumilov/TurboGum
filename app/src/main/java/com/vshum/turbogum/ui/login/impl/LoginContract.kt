package com.vshum.turbogum.ui.login.impl

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/** Contract between [com.vshum.turbogum.ui.login.LoginFragment] and its presenter. */
interface LoginContract {

    interface View {
        fun setLoading(loading: Boolean)
        fun onAuthSuccess(needsNickname: Boolean)
        fun onAuthError(message: String)
    }

    interface Presenter {
        fun signInEmail(email: String, password: String)
        fun registerEmail(email: String, password: String)
        fun onGoogleAccount(account: GoogleSignInAccount)
    }
}
