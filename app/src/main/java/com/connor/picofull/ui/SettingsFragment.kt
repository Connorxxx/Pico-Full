package com.connor.picofull.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.connor.picofull.R
import com.connor.picofull.constant.*
import com.connor.picofull.databinding.FragmentSettingsBinding
import com.connor.picofull.utils.getHexString
import com.connor.picofull.utils.logCat
import com.connor.picofull.utils.repeatOnStart
import com.connor.picofull.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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

        if (viewModel.settingsData.buzz) {
            binding.radioBuzzOn.isChecked = true
            viewModel.settingsData.fromUser = false
        } else {
            binding.radioBuzzOff.isChecked = true
            viewModel.settingsData.fromUser = false
        }
        binding.seekVolume.progress = viewModel.settingsData.volume
        viewModel.settingsData.language?.let { binding.rgLanguage.check(it) }
        binding.rgBuzz.setOnCheckedChangeListener { _, id ->
            lifecycleScope.launch {
                delay(100)
                "---------------from user ${viewModel.settingsData.fromUser}--------------".logCat()
                when (id) {
                    R.id.radio_buzz_off -> {
                        if (binding.radioBuzzOff.isChecked && viewModel.settingsData.fromUser) {
                            "buzz click off".logCat()
                            viewModel.sendHex(UPLOAD_BUZZ_OFF)
                            viewModel.settingsData.buzz = false
                            viewModel.settingsData.fromUser = false
                        }
                    }
                    R.id.radio_buzz_on -> {
                        if (binding.radioBuzzOn.isChecked && viewModel.settingsData.fromUser) {
                            "buzz click on".logCat()
                            viewModel.sendHex(UPLOAD_BUZZ_ON)
                            viewModel.settingsData.buzz = true
                            viewModel.settingsData.fromUser = false
                        }
                    }
                }
            }

        }
        binding.radioBuzzOff.setOnClickListener {
         //   if (!viewModel.settingsData.fromUser) {
                viewModel.settingsData.fromUser = true
                binding.radioBuzzOff.isChecked = true
          //  }
        }
        binding.radioBuzzOn.setOnClickListener {
          //  if (!viewModel.settingsData.fromUser) {
                viewModel.settingsData.fromUser = true
                binding.radioBuzzOn.isChecked = true
          //  }
        }
        binding.rgLanguage.setOnCheckedChangeListener { _, id ->
            viewModel.storeLanguage(id)
            lifecycleScope.launch {
                delay(250)
                findNavController().navigate(R.id.settingsFragment, arguments, NavOptions.Builder().setPopUpTo(R.id.settingsFragment,true).build())
            }
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
                        ISSUED_BUZZ_OFF -> {
                            viewModel.settingsData.fromUser = false
                            viewModel.settingsData.buzz = false
                            binding.radioBuzzOff.isChecked = true
                        }
                        ISSUED_BUZZ_ON -> {
                            viewModel.settingsData.fromUser = false
                            viewModel.settingsData.buzz = true
                            binding.radioBuzzOn.isChecked = true
                        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        "SettDestroy".logCat()
    }

}