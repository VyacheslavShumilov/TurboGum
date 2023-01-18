package com.vshum.turbogum.services

import com.vshum.turbogum.model.WrappersList
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface Api {
    @GET("dataLiners.json")
    fun getWrappersList(): Call<ArrayList<WrappersList>>

//    @GET("dataTurbo.json")
//    fun getTurbo(): Call<ArrayList<Liner>>
//
//    @GET("dataTurbo2000.json")
//    fun getTurbo2000(): Call<ArrayList<Liner>>
//
//    @GET("dataTurboClassic.json")
//    fun getTurboClassic(): Call<ArrayList<Liner>>
//
//    @GET("dataTurboLegends.json")
//    fun getTurboLegends(): Call<ArrayList<Liner>>

    companion object {
        var BASE_URL = "https://raw.githubusercontent.com/VyacheslavShumilov/JsonTurbo/main/"
        fun create(): Api {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(Api::class.java)
        }
    }
}