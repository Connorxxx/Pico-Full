package com.connor.picofull.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.connor.picofull.R
import com.connor.picofull.databinding.FragmentSettingsBinding
import com.connor.picofull.databinding.FragmentVideoBinding
import com.connor.picofull.models.VideoData
import com.connor.picofull.ui.adapter.VideoListAdapter
import com.connor.picofull.utils.logCat
import com.connor.picofull.viewmodels.MainViewModel
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var videoListAdapter: VideoListAdapter

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
        videoListAdapter.submitList(viewModel.videoList)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}