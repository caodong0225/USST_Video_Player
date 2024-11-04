package com.caodong0225.videoplayer

import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caodong0225.videoplayer.client.RetrofitClient
import com.caodong0225.videoplayer.client.RetrofitClient.BASE_URL
import com.caodong0225.videoplayer.client.RetrofitClient.sharedPreferences
import com.caodong0225.videoplayer.model.UploadVideoInfoDTO
import com.caodong0225.videoplayer.model.VideoInfo
import com.caodong0225.videoplayer.repository.UserRepository
import com.caodong0225.videoplayer.repository.VideoRepository
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.EventLogger
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private var jwtToken: String? = null
    private var videoPlay: VideoInfo? = null
    private val userRepository = UserRepository()  // 实例化 AuthRepository
    private val videoRepository = VideoRepository()  // 实例化 VideoRepository
    private var videoStartTime: Long? = null
    private var simpleCache: SimpleCache? = null
    private lateinit var cacheDataSourceFactory: CacheDataSource.Factory
    private var videoAPI = BASE_URL + "video?filename="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RetrofitClient.initialize(this)

        // 获取设备的唯一 ID 作为 UUID
        val androidId: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // 获取 JWT token
        CoroutineScope(Dispatchers.IO).launch {
            jwtToken = userRepository.fetchJwtToken(androidId)
            // 打印jwtToken
            // println("jwtToken: $jwtToken")
            if (jwtToken != null) {
                // 获取视频信息
                videoPlay = videoRepository.getRandomVideo(jwtToken!!)

                if(videoPlay == null) {
                    // 网络请求有问题，利用Toast弹出，然后退出应用
                    // Switch to the main thread to show the Toast and then finish the activity
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "没有更多视频了", Toast.LENGTH_SHORT).show()
                        // finish()
                    }
                    // finish()
                }
                // 打印videoPlay
                // println("videoPlayInfo: $videoPlay")
            } else {
                // 网络请求有问题，利用Toast弹出，然后退出应用
                // Switch to the main thread to show the Toast and then finish the activity
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "网络请求失败", Toast.LENGTH_SHORT).show()
                    // finish()
                }
                // finish()
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

        // 设置上一个按钮点击事件
        findViewById<Button>(R.id.previousButton).setOnClickListener {
            playPreviousVideo()
        }
    }

    private suspend fun playNextVideo() {
        // 播放下一个视频
        // val currentPosition = player.currentPosition // Current position in milliseconds
        if (videoPlay == null) {
            // 网络请求有问题，利用Toast弹出，然后退出应用
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "没有更多视频了", Toast.LENGTH_SHORT).show()
                // finish() // 取消退出逻辑，避免不必要的退出
            }
        }else{
            val duration = System.currentTimeMillis() - videoStartTime!!
            val videoId = videoPlay?.id
            // 设置为UpdateVideoInfo
            // 上传浏览数据历史
            videoRepository.saveVideoToHistory(videoPlay!!)
            videoRepository.uploadVideoInfo(jwtToken!!, UploadVideoInfoDTO(videoId!!, duration))
            // 刷新新的播放视频
            // 获取视频信息
            videoPlay = videoRepository.getRandomVideo(jwtToken!!)
            // 打印videoPlay
            // println("videoPlayInfo: $videoPlay")
            if (videoPlay == null) {
                // 网络请求有问题，利用Toast弹出，然后退出应用
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "没有更多视频了", Toast.LENGTH_SHORT).show()
                    // finish() // 取消退出逻辑，避免不必要的退出
                }
            } else {
                // 打印videoPlay
                // println("videoPlayInfo: $videoPlay")
                withContext(Dispatchers.Main) {
                    playVideo(videoAPI + (videoPlay?.videoName ?: ""))
                }
            }
        }
    }

    private fun playPreviousVideo() {
        val historyList = videoRepository.getVideoHistory()
        if (historyList.size > 1) {
            historyList.removeLast() // 移除当前视频，得到上一个视频
            val previousVideo = historyList.last()
            playVideo(videoAPI + previousVideo.videoName)

            // 更新历史记录，去掉当前视频
            val jsonString = Gson().toJson(historyList)
            sharedPreferences.edit().putString("video_history_list", jsonString).apply()
        } else {
            Toast.makeText(this, "没有更多历史记录", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializePlayer() {
        // Initialize cache
        val cacheSize: Long = 100 * 1024 * 1024 // 100 MB
        val cacheDir = File(cacheDir, "media")
        val evictor = LeastRecentlyUsedCacheEvictor(cacheSize)
        simpleCache = SimpleCache(cacheDir, evictor)

        // Create a DataSource.Factory with caching enabled
        val httpDataSourceFactory = DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(simpleCache!!)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

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
        playVideo(videoAPI + (videoPlay?.videoName ?: ""))
    }

    private fun playVideo(source: String) {


        val videoUri = Uri.parse(source)
        val mediaItem = MediaItem.fromUri(videoUri)

        // 创建 MediaSource 对象
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(mediaItem)

        // 设置媒体源并播放
        player.setMediaSource(mediaSource)

        // player.setMediaItem(mediaItem)
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