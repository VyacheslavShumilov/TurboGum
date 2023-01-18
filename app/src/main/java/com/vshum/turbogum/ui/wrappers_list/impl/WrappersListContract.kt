package com.vshum.turbogum.ui.wrappers_list.impl

import com.vshum.turbogum.model.WrappersList
import com.vshum.turbogum.mvp.BaseContract

interface WrappersListContract {
    interface View: BaseContract.View {
        fun onSuccessList(wrappersList: ArrayList<WrappersList>)
        fun error(errMessage: String)
        fun progress(show: Boolean)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun responseData()
    }
}