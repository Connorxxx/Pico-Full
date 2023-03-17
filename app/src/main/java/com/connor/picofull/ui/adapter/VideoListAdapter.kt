package com.connor.picofull.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.load
import coil.request.videoFrameMillis
import com.connor.picofull.R
import com.connor.picofull.databinding.ItemVideoInfoBinding
import com.connor.picofull.models.VideoInfo
import com.connor.picofull.utils.logCat
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class VideoListAdapter @Inject constructor(@ActivityContext val context: Context) :
    ListAdapter<VideoInfo, VideoListAdapter.ViewHolder>(FlowerDiffCallback) {

    object FlowerDiffCallback : DiffUtil.ItemCallback<VideoInfo>() {
        override fun areItemsTheSame(oldItem: VideoInfo, newItem: VideoInfo): Boolean {
            return oldItem.videoFile.name == newItem.videoFile.name
        }

        override fun areContentsTheSame(oldItem: VideoInfo, newItem: VideoInfo): Boolean {
            return oldItem == newItem
        }
    }

    var listener: ((VideoInfo) -> Unit?)? = null

    fun setClickListener(listener: (VideoInfo) -> Unit) {
        this.listener = listener
    }

    inner class ViewHolder(private val binding: ItemVideoInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cardVideo.setOnClickListener {
                binding.m?.let { info -> listener?.let { it(info) } }
            }
        }

        fun bind(videoInfo: VideoInfo) {
            binding.m = videoInfo
            binding.imgVideo.load(videoInfo.videoFile) {
                decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemVideoInfoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_video_info,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repo = getItem(position)
        holder.bind(repo)
    }
}