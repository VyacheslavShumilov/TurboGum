package com.vshum.turbogum.ui.nickname.impl

/** Contract between [com.vshum.turbogum.ui.nickname.NicknameSetupFragment] and its presenter. */
interface NicknameContract {

    interface View {
        fun setLoading(loading: Boolean)
        fun onSaved()
        fun onError(message: String)
    }

    interface Presenter {
        fun prefilledNickname(): String
        fun saveNickname(nickname: String)
    }
}
