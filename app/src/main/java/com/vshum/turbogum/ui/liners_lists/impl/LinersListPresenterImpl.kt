package com.vshum.turbogum.ui.liners_lists.impl

import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.services.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LinersListPresenterImpl : LinersListContract.Presenter {

    private var mvpView: LinersListContract.View? = null
    private var api = Api.create()


    override fun responseData() {
        mvpView?.let { view ->
            view.progress(true)
            api.getLinersList().enqueue(object : Callback<ArrayList<Liner>> {
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

    override fun attachView(view: LinersListContract.View) {
        mvpView = view
    }

    override fun detachView() {
        mvpView = null
    }
}