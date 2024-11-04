package com.caodong0225.videoplayer.repository

import com.caodong0225.videoplayer.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    // 使用协程在IO线程中执行网络请求
    suspend fun fetchJwtToken(uuid: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getJwtToken(uuid)
                // 这里可以记录响应的信息
                response.data
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}