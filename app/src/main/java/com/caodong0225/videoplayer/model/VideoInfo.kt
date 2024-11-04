package com.caodong0225.videoplayer.model

data class VideoInfo(
    /**
     * 主键
     */
    var id: Int,  // 使用 Int? 表示 id 可以为 null

    /**
     * 视频的名称
     */
    var videoName: String,  // 使用 String? 表示 videoName 可以为 null

    /**
     * 视频类型
     */
    var videoType: String,  // 使用 String? 表示 videoType 可以为 null

    /**
     * 视频的时长
     */
    var videoTime: Int  // 使用 Int? 表示 videoTime 可以为 null

)