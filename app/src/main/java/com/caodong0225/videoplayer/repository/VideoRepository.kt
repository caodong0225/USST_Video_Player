package com.caodong0225.videoplayer.repository

import com.caodong0225.videoplayer.client.RetrofitClient
import com.caodong0225.videoplayer.model.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoRepository {
    // 使用协程在IO线程中执行网络请求
    suspend fun getRandomVideo(token: String): VideoInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.videoService.getRandomVideo(token)
                // 这里可以记录响应的信息
                response.data
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}