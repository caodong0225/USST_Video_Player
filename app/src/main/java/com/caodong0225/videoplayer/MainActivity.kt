package com.caodong0225.videoplayer

import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.caodong0225.videoplayer.client.RetrofitClient.BASE_URL
import com.caodong0225.videoplayer.model.UploadVideoInfoDTO
import com.caodong0225.videoplayer.model.VideoInfo
import com.caodong0225.videoplayer.repository.UserRepository
import com.caodong0225.videoplayer.repository.VideoRepository
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
    private var videoPlay: VideoInfo? = null
    private val userRepository = UserRepository()  // 实例化 AuthRepository
    private val videoRepository = VideoRepository()  // 实例化 VideoRepository
    private var videoStartTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 获取设备的唯一 ID 作为 UUID
        val androidId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // 获取 JWT token
        CoroutineScope(Dispatchers.IO).launch {
            jwtToken = userRepository.fetchJwtToken(androidId)
            // 打印jwtToken
            // println("jwtToken: $jwtToken")
            if (jwtToken != null) {
                // 获取视频信息
                videoPlay = videoRepository.getRandomVideo(jwtToken!!)
                // 打印videoPlay
                // println("videoPlayInfo: $videoPlay")
            }else{
                // 网络请求有问题，利用Toast弹出，然后退出应用
                // Toast.makeText(this, "网络请求失败", Toast.LENGTH_SHORT).show()
                finish()
            }

            withContext(Dispatchers.Main) {
                initializePlayer()
            }
        }

        // 设置下一个按钮点击事件
        findViewById<Button>(R.id.nextButton).setOnClickListener {
            // 启动协程
            CoroutineScope(Dispatchers.IO).launch {
                playNextVideo()
            }
        }
    }

    private suspend fun playNextVideo() {
        // 播放下一个视频
        // val currentPosition = player.currentPosition // Current position in milliseconds
        val duration = System.currentTimeMillis() - videoStartTime!!
        val videoId = videoPlay?.id
        // 设置为UpdateVideoInfo
        // 上传浏览数据历史
        videoRepository.uploadVideoInfo(jwtToken!!, UploadVideoInfoDTO(videoId!!, duration))
        // 刷新新的播放视频
        // 获取视频信息
        videoPlay = videoRepository.getRandomVideo(jwtToken!!)
        // 打印videoPlay
        // println("videoPlayInfo: $videoPlay")
        withContext(Dispatchers.Main) {
            playVideo(BASE_URL + "video?filename=" + (videoPlay?.videoName ?: ""))
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

        // 初始化videoPlay
        // val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.sample_video}")
        // val videoUri = Uri.parse("http://192.168.31.62:8080/video?filename=loading.mp4")
        playVideo(BASE_URL + "video?filename=" + (videoPlay?.videoName ?: ""))
    }

    private fun playVideo(source: String) {
        val videoUri = Uri.parse(source)
        val mediaItem = MediaItem.fromUri(videoUri)

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        videoStartTime = System.currentTimeMillis()

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