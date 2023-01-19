package com.vshum.turbogum.ui.liners_lists.impl

import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.mvp.BaseContract

interface LinersListContract {
    interface View: BaseContract.View {
        fun onSuccessList(linersList: ArrayList<Liner>)
        fun error(errMessage: String)
        fun progress(show: Boolean)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun responseData()
    }
}