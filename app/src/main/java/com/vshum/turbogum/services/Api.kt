package com.vshum.turbogum.services

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface Api {
    @GET("dataTurbo.json")
    fun getTurbo(): Call<ArrayList<Turbo>>

    @GET("dataTurbo2000.json")
    fun getTurbo2000(): Call<ArrayList<Turbo2000>>

    @GET("dataTurboClassic.json")
    fun getTurboClassic(): Call<ArrayList<TurboClassic>>

    @GET("dataTurboLegends.json")
    fun getTurboLegends(): Call<ArrayList<TurboLegends>>

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