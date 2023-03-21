package com.connor.picofull.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.connor.picofull.R
import com.connor.picofull.constant.*
import com.connor.picofull.databinding.FragmentBackstageBinding
import com.connor.picofull.databinding.FragmentHomeBinding
import com.connor.picofull.utils.getHexString
import com.connor.picofull.utils.logCat
import com.connor.picofull.utils.repeatOnStart
import com.connor.picofull.utils.showToast
import com.connor.picofull.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BackstageFragment : Fragment() {

    private var _binding: FragmentBackstageBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBackstageBinding.inflate(inflater, container, false)

        initUI()
        initClick()
        repeatOnStart {
            launch {
                viewModel.receiveEvent.collect {
                    if (it == ISSUED_CLEAR) "清除成功".showToast()
                    if (it.contains(ISSUED_LOGIN_X)) {
                        when (it.substring(it.length - 2).toInt(16)) {
                            0 -> binding.toggleLogin.check(R.id.btn_open)
                            1 -> binding.toggleLogin.check(R.id.btn_close)
                        }
                    }
                    if (it.contains(ISSUED_ENERGY_BACKSTAGE_XX_X)) {
                        it.substring(it.length - 4).toInt(16).also { v ->
                            binding.tvEnergyValue.text = v.toString()
                            viewModel.backstageData.energy = v
                        }
                    }
                    if (it.contains(ISSUED_VOLTAGE_XX_X)) {
                        it.substring(it.length - 4).toInt(16).also { v ->
                            binding.tvVoltageValue.text = v.toString()
                            viewModel.backstageData.voltage = v
                        }
                    }
                }
            }
        }
        return binding.root
    }

    private fun initClick() {
        binding.btnClear.setOnClickListener {
            viewModel.sendHex(UPLOAD_CLEAR)
        }
        binding.toggleLogin.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                viewModel.backstageData.loginId = checkedId
                when (checkedId) {
                    R.id.btn_open -> viewModel.sendHex(UPLOAD_LOGIN_X + "00")
                    R.id.btn_close -> viewModel.sendHex(UPLOAD_LOGIN_X + "01")
                }
            }
        }
        binding.btnEnergyPlus.setOnClickListener {
            viewModel.sendHex(UPLOAD_ENERGY_BACKSTAGE_X_XX + "01")
            (++viewModel.backstageData.energy).also {
                binding.tvEnergyValue.text = getString(R.string.energy_value, it)
            }
        }
        binding.btnEnergyMinus.setOnClickListener {
            if (viewModel.backstageData.energy <= 0) return@setOnClickListener
            viewModel.sendHex(UPLOAD_ENERGY_BACKSTAGE_X_XX + "00")
            (--viewModel.backstageData.energy).also {
                binding.tvEnergyValue.text = getString(R.string.energy_value, it)
            }
        }
        binding.btnVoltagePlus.setOnClickListener {
            (++viewModel.backstageData.voltage).also {
                viewModel.sendHex(UPLOAD_VOLTAGE_X_XX + it.getHexString(2))
                binding.tvVoltageValue.text = getString(R.string.voltage_value, it)
            }
        }
        binding.btnVoltageMinus.setOnClickListener {
            if (viewModel.backstageData.voltage <= 0) return@setOnClickListener
            (--viewModel.backstageData.voltage).also {
                viewModel.sendHex(UPLOAD_VOLTAGE_X_XX + it.getHexString(2))
                binding.tvVoltageValue.text = getString(R.string.voltage_value, it)
            }
        }
    }

    private fun initUI() {
        viewModel.backstageData.loginId?.let { binding.toggleLogin.check(it) }
        binding.tvEnergyValue.text = viewModel.backstageData.energy.toString()
        binding.tvVoltageValue.text = viewModel.backstageData.voltage.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}