package com.vshum.turbogum.ui.liners_lists.turbo.impl

import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.services.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TurboPresenterImpl : TurboContract.Presenter {

    private var mvpView: TurboContract.View? = null
    private var api = Api.create()


    override fun responseData() {
        mvpView?.let { view ->
            view.progress(true)
            api.getWrappersList().enqueue(object : Callback<ArrayList<Liner>> {
                override fun onResponse(
                    call: Call<ArrayList<Liner>>,
                    response: Response<ArrayList<Liner>>
                ) {
                    if (response.isSuccessful) {
                        view.progress(false)
                        response.body()?.let { data ->
                            view.onSuccessList(data)
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<Liner>>, t: Throwable) {
                    view.progress(false)
                    view.error("No internet")
                }
            })
        }
    }

    override fun attachView(view: TurboContract.View) {
        mvpView = view
    }

    override fun detachView() {
        mvpView = null
    }
}