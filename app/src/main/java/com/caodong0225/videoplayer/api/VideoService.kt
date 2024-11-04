package com.caodong0225.videoplayer.api

import com.caodong0225.videoplayer.model.BaseDataResponse
import com.caodong0225.videoplayer.model.UploadVideoInfoDTO
import com.caodong0225.videoplayer.model.VideoInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface VideoService {
    @GET("play")
    suspend fun getRandomVideo(@Header("Authorization") token: String): BaseDataResponse<VideoInfo>

    @POST("video/visit")
    suspend fun uploadVideoInfo(
        @Header("Authorization") token: String,
        @Body videoInfo: UploadVideoInfoDTO
    ): BaseDataResponse<String>
}