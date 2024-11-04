package com.caodong0225.videoplayer

import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.caodong0225.videoplayer.client.RetrofitClient
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.EventLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private var jwtToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 获取设备的唯一 ID 作为 UUID
        val androidId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // 获取 JWT token
        CoroutineScope(Dispatchers.IO).launch {
            jwtToken = fetchJwtToken(androidId)

            withContext(Dispatchers.Main) {
                initializePlayer()
            }
        }
    }

    private suspend fun fetchJwtToken(uuid: String): String? {
        return try {
            val response = RetrofitClient.instance.getJwtToken(uuid)
            // Log.d("JWT Response", "Code: ${response.code}, Message: ${response.message}, Data: ${response.data}")
            response.data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun initializePlayer() {
        // 初始化 VideoView 和按钮
        playerView = findViewById(R.id.playerView)

        // 初始化 ExoPlayer
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        // 添加事件监听器以获取详细日志
        // 添加事件监听器以获取详细日志
        player.addAnalyticsListener(EventLogger())

        // val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.sample_video}")
        val videoUri = Uri.parse("http://192.168.31.62:8080/video?filename=loading.mp4")
        val mediaItem = MediaItem.fromUri(videoUri)

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play();
    }

    override fun onPause() {
        super.onPause()
        player.pause()  // 暂停播放
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()  // 释放资源
    }
}