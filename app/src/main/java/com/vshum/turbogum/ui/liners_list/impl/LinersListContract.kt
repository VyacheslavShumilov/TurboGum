package com.vshum.turbogum.ui.liners_list.impl

import com.vshum.turbogum.model.LinersList
import com.vshum.turbogum.mvp.BaseContract

interface LinersListContract {
    interface View: BaseContract.View {
        fun onSuccessList(linersList: ArrayList<LinersList>)
        fun error(errMessage: String)
        fun progress(show: Boolean)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun responseData()
    }
}