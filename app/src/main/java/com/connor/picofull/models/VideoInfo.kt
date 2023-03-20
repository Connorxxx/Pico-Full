package com.connor.picofull.models

import kotlinx.serialization.Serializable
import java.io.File

data class VideoInfo(
    val videoFile: File,
    val videoName: String,
    val videoLength: String
)
@Serializable
data class VideoData(
    val videoName: String,
    val videoPath: String
)
