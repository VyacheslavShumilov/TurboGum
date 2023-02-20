package com.vshum.turbogum.services

import com.vshum.turbogum.Constants
import com.vshum.turbogum.model.Liner
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface Api {

    @GET(Constants.JSON)
    fun getLinersList(): Call<ArrayList<Liner>>

    companion object {
        var BASE_URL = Constants.URL
        fun create(): Api {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(Api::class.java)
        }
    }
}