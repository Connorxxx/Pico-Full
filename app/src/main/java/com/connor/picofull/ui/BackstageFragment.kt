package com.connor.picofull.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.connor.picofull.R
import com.connor.picofull.constant.*
import com.connor.picofull.databinding.FragmentBackstageBinding
import com.connor.picofull.models.type.BtnType
import com.connor.picofull.models.type.onLogin
import com.connor.picofull.ui.dialog.AlertDialogFragment
import com.connor.picofull.ui.dialog.ExitDialogFragment
import com.connor.picofull.utils.getHexString
import com.connor.picofull.utils.logCat
import com.connor.picofull.utils.repeatOnStart
import com.connor.picofull.utils.showToast
import com.connor.picofull.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
                viewModel.btnEvent.collect { type ->
                    with(type) {
                        onLogin {
                            viewModel.backstageData.loginState = it
                            loginState(it)
                        }
                    }
                }
            }
            launch {
                viewModel.receiveEvent.collect {
                    if (it == ISSUED_CLEAR) {
                        viewModel.homeData.pulse = 0
                        binding.tvCount.text = ""
                    }
                    if (it.contains(ISSUED_LOGIN_X)) {
                        when (it.substring(it.length - 2).toInt(16)) {
                            0 -> viewModel.senBtnEvent(BtnType.Login(true))
                            1 -> viewModel.senBtnEvent(BtnType.Login(false))
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
                    if (it.contains(ISSUES_PULSE_XXXX)) {
                        delay(90)
                        binding.tvCount.text = viewModel.homeData.pulse.toString()
                    }
                }
            }
        }
        return binding.root
    }

    private fun loginState(it: Boolean) {
        binding.toggleLogin.load(if (it) R.drawable.img_bs_on_a else R.drawable.img_bs_off_a)
    }

    private fun initClick() {
        binding.btnClear.setOnClickListener {
            viewModel.sendHex(UPLOAD_CLEAR)
            viewModel.homeData.pulse = 0
            binding.tvCount.text = ""
//            viewModel.backstageData.clear = true
//            binding.btnClear.load(R.drawable.img_bg_clear_on)
        }
        binding.toggleLogin.setOnClickListener {
            viewModel.backstageData.loginState.also {
                viewModel.senBtnEvent(BtnType.Login(!it))
                "start Click ${!it}".logCat()
                if (!it) viewModel.sendHex(UPLOAD_LOGIN_X + "00") else viewModel.sendHex(UPLOAD_LOGIN_X + "01")
            }
        }
        binding.btnEnergyPlus.setOnClickListener {
           // if (viewModel.backstageData.energy >= 10) return@setOnClickListener
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
         //   if (viewModel.backstageData.voltage >= 10) return@setOnClickListener
            (++viewModel.backstageData.voltage).also {
                viewModel.sendHex(UPLOAD_VOLTAGE_X_XX + it.getHexString(2))
                binding.tvVoltageValue.text = it.toString()
            }
        }
        binding.btnVoltageMinus.setOnClickListener {
            if (viewModel.backstageData.voltage <= 0) return@setOnClickListener
            (--viewModel.backstageData.voltage).also {
                viewModel.sendHex(UPLOAD_VOLTAGE_X_XX + it.getHexString(2))
                binding.tvVoltageValue.text = it.toString()
            }
        }
//        binding.btnSaveSerial.setOnClickListener {
//            binding.editSerial.text.toString().also {
//                if (it != viewModel.serialPort.toString()) {
//                    if (it.matches("[0-9]+".toRegex())) {
//                        viewModel.storeSerial(it.toInt())
//                    }
//                }
//            }
//        }

        viewModel.items.indexOf(viewModel.serialPort).also {
            "posiotn $it".logCat()
        }

        binding.editSerial.post {
            binding.editSerial.setSelection(viewModel.items.indexOf(viewModel.serialPort), true)

            binding.editSerial.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    viewModel.items[pos].logCat()
                    viewModel.storeSerial(viewModel.items[pos])
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, viewModel.items)
        binding.editSerial.adapter = adapter

        binding.btnExitApp.setOnClickListener {
            ExitDialogFragment().show(childFragmentManager, "AlertDialogFragment.TAG")
        }
    }

    private fun initUI() {
        loginState(viewModel.backstageData.loginState)
        binding.tvEnergyValue.text = viewModel.backstageData.energy.toString()
        binding.tvVoltageValue.text = viewModel.backstageData.voltage.toString()
        //if (viewModel.backstageData.clear) binding.btnClear.load(R.drawable.img_bg_clear_on)
        if (viewModel.homeData.pulse != 0L) {
            binding.tvCount.text = viewModel.homeData.pulse.toString()
        }
       // binding.editSerial.setText(viewModel.serialPort.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}