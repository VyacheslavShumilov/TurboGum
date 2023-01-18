package com.vshum.turbogum.ui.liners_lists.turbo.impl

import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.mvp.BaseContract

interface TurboContract {
    interface View: BaseContract.View {
        fun onSuccessList(linersTurboList: ArrayList<Liner>)
        fun error(errMessage: String)
        fun progress(show: Boolean)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun responseData()
    }
}