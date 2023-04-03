package com.connor.picofull.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.connor.picofull.R
import com.connor.picofull.constant.*
import com.connor.picofull.databinding.FragmentSettingsBinding
import com.connor.picofull.utils.getHexString
import com.connor.picofull.utils.repeatOnStart
import com.connor.picofull.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        if (viewModel.settingsData.buzz) binding.radioBuzzOn.isChecked =
            true else binding.radioBuzzOff.isChecked = true
        binding.seekVolume.progress = viewModel.settingsData.volume
        viewModel.settingsData.language?.let { binding.rgLanguage.check(it) }
        binding.rgBuzz.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.radio_buzz_off -> {
                    viewModel.sendHex(UPLOAD_BUZZ_OFF)
                    viewModel.settingsData.buzz = false
                }
                R.id.radio_buzz_on -> {
                    viewModel.sendHex(UPLOAD_BUZZ_ON)
                    viewModel.settingsData.buzz = true
                }
            }
        }
        binding.rgLanguage.setOnCheckedChangeListener { _, id ->
            viewModel.storeLanguage(id)
        }
//        binding.seekVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
//            override fun onStartTrackingTouch(p0: SeekBar?) {}
//            override fun onStopTrackingTouch(v: SeekBar?) {
//                v?.progress?.let {
//                    viewModel.settingsData.volume = it
//                    viewModel.sendHex(UPLOAD_VOLUME_X + it.getHexString(2))
//                }
//            }
//        })
        binding.seekVolume.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }
        binding.imgVolumeMinus.setOnClickListener {
            if (viewModel.settingsData.volume <= 0) return@setOnClickListener
            viewModel.sendHex(UPLOAD_VOLUME_X + (viewModel.settingsData.volume - 1).getHexString(2))
            viewModel.settingsData.volume--
            binding.seekVolume.progress = viewModel.settingsData.volume
        }
        binding.imgVolumePlus.setOnClickListener {
            if (viewModel.settingsData.volume >= 10) return@setOnClickListener
            viewModel.sendHex(UPLOAD_VOLUME_X + (viewModel.settingsData.volume + 1).getHexString(2))
            viewModel.settingsData.volume++
            binding.seekVolume.progress = viewModel.settingsData.volume
        }

        repeatOnStart {
            launch {
                viewModel.receiveEvent.collect {
                    when (it) {
                        ISSUED_BUZZ_OFF -> binding.radioBuzzOff.isChecked = true
                        ISSUED_BUZZ_ON -> binding.radioBuzzOn.isChecked = true
                    }
                    if (it.contains(ISSUED_VOLUME_X)) {
                        val value = it.substring(it.length - 2).toInt(16)
                        if (value < 0 || value > 10) return@collect
                        viewModel.settingsData.volume = value
                        binding.seekVolume.progress = value
                    }
                }
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}