package com.caodong0225.videoplayer.client

import com.caodong0225.videoplayer.api.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.31.62:8080/"

    val instance: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // 用于解析 JSON
            .build()
            .create(AuthService::class.java)
    }
}