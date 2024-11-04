package com.caodong0225.videoplayer.model

data class BaseDataResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)