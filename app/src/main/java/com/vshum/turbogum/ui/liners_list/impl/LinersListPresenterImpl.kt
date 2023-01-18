package com.vshum.turbogum.ui.liners_list.impl

import com.vshum.turbogum.model.LinersList
import com.vshum.turbogum.services.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LinersListPresenterImpl: LinersListContract.Presenter {

    private var mvpView: LinersListContract.View? = null
    private var api = Api.create()


    override fun responseData() {
        mvpView?.let { view ->
            view.progress(true)
            api.getLinersList().enqueue(object: Callback<ArrayList<LinersList>> {
                override fun onResponse(
                    call: Call<ArrayList<LinersList>>,
                    response: Response<ArrayList<LinersList>>
                ) {
                    if (response.isSuccessful) {
                        view.progress(false)
                        response.body()?.let { data ->
                            view.onSuccessList(data)
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<LinersList>>, t: Throwable) {
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