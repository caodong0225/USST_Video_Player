package com.caodong0225.videoplayer

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化 VideoView 和按钮
        videoView = findViewById(R.id.videoView)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)

        // 设置视频的 URI，可以使用本地文件路径或网络URL
        val videoUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.sample_video)
        videoView.setVideoURI(videoUri)

        // 播放按钮点击事件
        playButton.setOnClickListener {
            if (!videoView.isPlaying) {
                videoView.start()
            }
        }

        // 暂停按钮点击事件
        pauseButton.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
            }
        }

        // 停止按钮点击事件
        stopButton.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.stopPlayback()
                videoView.resume()  // 让视频重新从头开始播放
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (videoView.isPlaying) {
            videoView.pause()  // 暂停视频播放
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()  // 停止并释放资源
    }
}