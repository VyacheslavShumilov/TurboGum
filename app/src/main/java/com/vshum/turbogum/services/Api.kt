package com.vshum.turbogum.services

import com.vshum.turbogum.Constants
import com.vshum.turbogum.model.Liner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface Api {
    @GET(Constants.JSON_LINERS)
    fun getLinersList(): Call<ArrayList<Liner>>

    companion object {
        var BASE_URL = Constants.URL

        fun create(): Api {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // Add the OkHttpClient with the interceptor
                .build()
                .create(Api::class.java)
        }
    }
}

/***
 * С этой модификацией HttpLoggingInterceptor будет регистрировать информацию о запросе и ответе, включая заголовки,
 * URL-адрес запроса и текст ответа, в logcat.
 */


//interface Api {
//
//    @GET(Constants.JSON)
//    fun getLinersList(): Call<ArrayList<Liner>>
//
//    companion object {
//        var BASE_URL = Constants.URL
//        fun create(): Api {
//            val retrofit = Retrofit.Builder()
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl(BASE_URL)
//                .build()
//            return retrofit.create(Api::class.java)
//        }
//    }
//}