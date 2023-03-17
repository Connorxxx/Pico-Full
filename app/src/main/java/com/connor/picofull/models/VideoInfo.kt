package com.connor.picofull.models

import java.io.File

data class VideoInfo(
    val videoFile: File,
    val videoName: String,
    val videoLength: String
)
