package com.caodong0225.videoplayer.model

data class BaseDataResponse<T>(
    val data: T? = null,
    val code: Int,
    val message: String
)