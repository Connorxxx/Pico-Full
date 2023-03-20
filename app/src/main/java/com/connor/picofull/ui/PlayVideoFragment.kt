package com.connor.picofull.ui

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.connor.picofull.MainActivity
import com.connor.picofull.R
import com.connor.picofull.databinding.FragmentPlayVideoBinding
import com.connor.picofull.databinding.FragmentVideoBinding
import com.connor.picofull.databinding.LayoutVideoControlBinding
import com.connor.picofull.models.VideoData
import com.connor.picofull.utils.logCat
import com.connor.picofull.viewmodels.MainViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class PlayVideoFragment : Fragment() {

    private var _binding: FragmentPlayVideoBinding? = null
    private var _videoBinding: LayoutVideoControlBinding? = null
    private val binding get() = _binding!!
    private val videoBinding get() = _videoBinding!!

    private val args: PlayVideoFragmentArgs by navArgs()

    private val viewModel by activityViewModels<MainViewModel>()

    private val player by lazy { ExoPlayer.Builder(requireContext()).build() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayVideoBinding.inflate(inflater, container, false)
        val view = binding.root
        _videoBinding = LayoutVideoControlBinding.bind(view)
        binding.playerView.player = player
        binding.playerControlView.player = binding.playerView.player
        val data = Json.decodeFromString<VideoData>(args.data)
        (activity as AppCompatActivity).supportActionBar?.title = data.videoName
        val uri = Uri.parse(data.videoPath)
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_playVideoFragment_to_videoFragment)
        }
        val audioManager  = requireActivity().getSystemService(AUDIO_SERVICE) as AudioManager
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val curr = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        with(videoBinding.seekBarVolume) {
            this.max = max
            progress = curr
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, i: Int, p2: Boolean) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,i,0)
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
        return view
    }

    override fun onStop() {
        super.onStop()
        player.stop()
        player.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
        _binding = null
        _videoBinding = null
    }

}