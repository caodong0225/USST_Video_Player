package com.caodong0225.videoplayer

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.EventLogger


class MainActivity : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化 VideoView 和按钮
        playerView = findViewById(R.id.playerView)

        // 初始化 ExoPlayer
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        // 添加事件监听器以获取详细日志
        // 添加事件监听器以获取详细日志
        player.addAnalyticsListener(EventLogger())

        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.sample_video}")
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