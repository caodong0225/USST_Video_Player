package com.caodong0225.videoplayer.api

import com.caodong0225.videoplayer.model.BaseDataResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthService {
    @GET("user")
    suspend fun getJwtToken(@Query("uuid") uuid: String): BaseDataResponse<String>
}
