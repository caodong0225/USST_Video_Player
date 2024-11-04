package com.caodong0225.videoplayer.client

import com.caodong0225.videoplayer.api.UserService
import com.caodong0225.videoplayer.api.VideoService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    const val BASE_URL = "http://192.168.31.62:8080/"

    val instance: UserService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // 用于解析 JSON
            .build()
            .create(UserService::class.java)
    }

    val videoService: VideoService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VideoService::class.java)
    }
}