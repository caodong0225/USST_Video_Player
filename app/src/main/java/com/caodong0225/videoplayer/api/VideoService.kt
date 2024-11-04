package com.caodong0225.videoplayer.api

import com.caodong0225.videoplayer.model.BaseDataResponse
import com.caodong0225.videoplayer.model.VideoInfo
import retrofit2.http.GET
import retrofit2.http.Header

interface VideoService {
    @GET("play")
    suspend fun getRandomVideo(@Header("Authorization") token: String): BaseDataResponse<VideoInfo>
}