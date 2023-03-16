package com.connor.picofull.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.*
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.connor.picofull.R
import com.connor.picofull.constant.*
import com.connor.picofull.databinding.FragmentHomeBinding
import com.connor.picofull.datastores.DataStoreManager
import com.connor.picofull.utils.getHexString
import com.connor.picofull.utils.logCat
import com.connor.picofull.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setEnergy()
        setRate()
        binding.tvPulse.text = getString(R.string.pulse, viewModel.homeData.pulse)
        binding.tvSpot.text = getString(R.string.spot, viewModel.homeData.spot)
        binding.toggleSwitch.isChecked = viewModel.homeData.switch
        binding.seekRedLight.progress = viewModel.homeData.readLight
        viewModel.homeData.waveId?.let {
            binding.toggleGroup.check(it)
        }


        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                viewModel.homeData.waveId = checkedId
                when (checkedId) {
                    R.id.btn_1064 -> viewModel.sendHex(UPLOAD_1064)
                    R.id.btn_523 -> viewModel.sendHex(UPLOAD_532)
                }
            }
        }
        binding.btnEnergyPlus.setOnClickListener { viewModel.sendHex(UPLOAD_ENERGY_PLUS) }
        binding.btnEnergyMinus.setOnClickListener { viewModel.sendHex(UPLOAD_ENERGY_MINUS) }
        binding.btnRatePlus.setOnClickListener {
            if (viewModel.homeData.rate >= 10) return@setOnClickListener
            viewModel.sendHex(UPLOAD_RATE_XX + (viewModel.homeData.rate + 1).getHexString())
            viewModel.homeData.rate++
        }
        binding.btnRateMinus.setOnClickListener {
            if (viewModel.homeData.rate <= 1) return@setOnClickListener
            viewModel.sendHex(UPLOAD_RATE_XX + (viewModel.homeData.rate - 1).getHexString())
            viewModel.homeData.rate--
        }
        binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.homeData.switch = isChecked
            if (viewModel.homeData.switch) viewModel.sendHex(UPLOAD_ON)
            else viewModel.sendHex(UPLOAD_OFF)
        }
        binding.btnFlash.setOnClickListener { viewModel.sendHex(UPLOAD_FLASH) }
        binding.btnAod.setOnClickListener { viewModel.sendHex(UPLOAD_ADD) }
        binding.btnOff.setOnClickListener { viewModel.sendHex(UPLOAD_CLOSE) }
        binding.seekRedLight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, i: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(v: SeekBar?) {
                 v?.progress?.let {
                     viewModel.homeData.readLight = it
                     viewModel.sendHex(UPLOAD_RED_LIGHT_XX + it.getHexString())
                 }

            }
        })
        binding.imgRedlightPlus.setOnClickListener {
            if (viewModel.homeData.readLight >= 10) return@setOnClickListener
            viewModel.sendHex(UPLOAD_RED_LIGHT_XX + (viewModel.homeData.readLight + 1).getHexString())
            viewModel.homeData.readLight++
            binding.seekRedLight.progress = viewModel.homeData.readLight
        }
        binding.imgReadlightMinus.setOnClickListener {
            if (viewModel.homeData.readLight <= 0) return@setOnClickListener
            viewModel.sendHex(UPLOAD_RED_LIGHT_XX + (viewModel.homeData.readLight - 1).getHexString())
            viewModel.homeData.readLight--
            binding.seekRedLight.progress = viewModel.homeData.readLight
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(STARTED) {
                launch {
                    viewModel.receiveEvent.collect {
                        when (it) {
                            ISSUED_1064 -> binding.toggleGroup.check(R.id.btn_1064)
                            ISSUED_532 -> binding.toggleGroup.check(R.id.btn_523)
                            ISSUED_ON -> {
                                viewModel.homeData.switch = true
                                binding.toggleSwitch.isChecked = true
                            }
                            ISSUED_OFF -> {
                                viewModel.homeData.switch = false
                                binding.toggleSwitch.isChecked = false
                            }
                        }
                        if (it.contains(ISSUED_ENERGY_XX)) {
                            val value = it.substring(it.length - 4).toInt(16)
                            viewModel.homeData.energy = value
                            setEnergy()
                        }
                        if (it.contains(ISSUED_RATE_XX)) {
                            val value = it.substring(it.length - 4).toInt(16)
                            viewModel.homeData.rate = value
                            setRate()
                        }
                        if (it.contains(ISSUES_PULSE_XXXX)) {
                            val value = it.substring(it.length - 8).toInt(16)
                            viewModel.homeData.pulse = value
                            binding.tvPulse.text = getString(R.string.pulse, value)
                        }
                        if (it.contains(ISSUED_SPOT_XX)) {
                            val value = it.substring(it.length - 4).toInt(16)
                            viewModel.homeData.spot = value
                            binding.tvSpot.text = getString(R.string.spot, value)
                        }
                        if (it.contains(ISSUED_RED_LIGHT_XX)) {
                            val value = it.substring(it.length - 4).toInt(16)
                            viewModel.homeData.readLight = value
                            binding.seekRedLight.progress = value
                        }
                    }
                }
            }
        }
        return binding.root
    }

    private fun setRate() {
        binding.tvHz.text = getString(R.string.hz, viewModel.homeData.rate)
        binding.progressHz.setPercent(viewModel.homeData.rate * 10)
    }

    private fun setEnergy() {
        binding.tvEnergy.text = getString(R.string.energy, viewModel.homeData.energy)
        binding.progressEnergy.setPercent(viewModel.homeData.energy * 10)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}