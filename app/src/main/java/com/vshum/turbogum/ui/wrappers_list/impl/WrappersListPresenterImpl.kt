package com.vshum.turbogum.ui.wrappers_list.impl

import com.vshum.turbogum.model.WrappersList
import com.vshum.turbogum.services.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WrappersListPresenterImpl: WrappersListContract.Presenter {

    private var mvpView: WrappersListContract.View? = null
    private var api = Api.create()


    override fun responseData() {
        mvpView?.let { view ->
            view.progress(true)
            api.getWrappersList().enqueue(object: Callback<ArrayList<WrappersList>> {
                override fun onResponse(
                    call: Call<ArrayList<WrappersList>>,
                    response: Response<ArrayList<WrappersList>>
                ) {
                    if (response.isSuccessful) {
                        view.progress(false)
                        response.body()?.let { data ->
                            view.onSuccessList(data)
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<WrappersList>>, t: Throwable) {
                    view.progress(false)
                    view.error("No internet")
                }

            })
        }
    }

    override fun attachView(view: WrappersListContract.View) {
        mvpView = view
    }

    override fun detachView() {
        mvpView = null
    }
}