package com.caodong0225.videoplayer.repository

import com.caodong0225.videoplayer.client.RetrofitClient
import com.caodong0225.videoplayer.client.RetrofitClient.sharedPreferences
import com.caodong0225.videoplayer.model.UploadVideoInfoDTO
import com.caodong0225.videoplayer.model.VideoInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    suspend fun uploadVideoInfo(token: String, videoInfo: UploadVideoInfoDTO): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.videoService.uploadVideoInfo(token, videoInfo)
                // 这里可以记录响应的信息
                response.data
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun saveVideoToHistory(videoInfo: VideoInfo) {
        val editor = sharedPreferences.edit()
        val historyList = getVideoHistory() // 获取当前历史记录
        historyList.add(videoInfo) // 将新的视频信息添加到列表中

        // 将更新后的列表存储回 SharedPreferences
        val jsonString = Gson().toJson(historyList)
        editor.putString("video_history_list", jsonString)
        editor.apply()
    }

    fun getVideoHistory(): MutableList<VideoInfo> {
        val jsonString = sharedPreferences.getString("video_history_list", null)
        return if (jsonString != null) {
            val type = object : TypeToken<MutableList<VideoInfo>>() {}.type
            Gson().fromJson(jsonString, type)
        } else {
            mutableListOf()
        }
    }
}