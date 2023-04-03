package com.connor.picofull.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.connor.picofull.constant.videoPath
import com.connor.picofull.databinding.FragmentVideoBinding
import com.connor.picofull.models.VideoData
import com.connor.picofull.models.VideoInfo
import com.connor.picofull.ui.adapter.VideoListAdapter
import com.connor.picofull.utils.*
import com.connor.picofull.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var videoListAdapter: VideoListAdapter

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        with(binding.rvVideo) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = videoListAdapter
        }
        videoListAdapter.setClickListener {
            val m = VideoData(it.videoName, it.videoFile.absolutePath)
            val data = Json.encodeToString(m)
            val action = VideoFragmentDirections.actionVideoFragmentToPlayVideoFragment(data)
            findNavController().navigate(action)
        }
        lifecycleScope.launch {
            viewModel.videoList.clear()
            File(videoPath).getAllFiles().onEach { file ->
                viewModel.videoList.add(
                    VideoInfo(
                        file,
                        file.name.substring(0, file.name.lastIndexOf(".")),
                        file.getVideoDuration().formatDuration()
                    )
                )
            }
            viewModel.videoList.size.logCat()
            videoListAdapter.submitList(ArrayList(viewModel.videoList))
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}