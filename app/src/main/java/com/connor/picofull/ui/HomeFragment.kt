package com.connor.picofull.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.connor.picofull.R
import com.connor.picofull.constant.*
import com.connor.picofull.databinding.FragmentHomeBinding
import com.connor.picofull.models.type.BtnType
import com.connor.picofull.models.type.onEnergy
import com.connor.picofull.models.type.onRate
import com.connor.picofull.models.type.onStart
import com.connor.picofull.utils.getHexString
import com.connor.picofull.utils.logCat
import com.connor.picofull.utils.repeatOnStart
import com.connor.picofull.utils.showToast
import com.connor.picofull.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        initUIState()
        setOnClick()
        initScope()
        return binding.root
    }

    private fun initScope() {
        repeatOnStart {
            launch {
                viewModel.waveState.collect {
                    "test $it".logCat()
                    viewModel.homeData.waveId = it
                    if (!it) binding.toggleGroup.load(R.drawable.img_1064) else binding.toggleGroup.load(R.drawable.img_532)
                }
            }
            launch {
                viewModel.btnEvent.collect { type ->
                    with(type) {
                        onEnergy {
                            energyBtnState(it)
                            viewModel.homeData.energyBtn = it
                        }
                        onRate {
                            rateBtnState(it)
                            viewModel.homeData.rateBtn = it
                        }
                        onStart {
                            startState(it)
                            viewModel.homeData.switch = it
                            "start $it".logCat()
                        }
                    }
                }
            }
            launch {
                viewModel.receiveEvent.collect {
                    when (it) {
                        ISSUED_1064 -> viewModel.storeWave(false) //binding.toggleGroup.check(R.id.btn_1064)
                        ISSUED_532 -> viewModel.storeWave(true) //binding.toggleGroup.check(R.id.btn_523)
                        ISSUED_ON -> {
                            viewModel.senBtnEvent(BtnType.Start(true))
                           // viewModel.homeData.switch = true
                        }
                        ISSUED_OFF -> {
                            viewModel.senBtnEvent(BtnType.Start(false))
                            //viewModel.homeData.switch = false
                        }
                        ISSUED_FLASH -> getString(R.string.flash).showToast()
                        ISSUED_AOD -> getString(R.string.aod).showToast()
                        ISSUED_CLOSE -> getString(R.string.off).showToast()
                    }
                    if (it.contains(ISSUED_ENERGY_XX)) {
                        val value = it.substring(it.length - 4).toInt(16)
                        if (value > 10) return@collect
                        viewModel.homeData.energy = value
                        setEnergy()
                        binding.progressHz.setScale(viewModel.homeData.rate * 4)
                    }
                    if (it.contains(ISSUED_RATE_XX)) {
                        val value = it.substring(it.length - 4).toInt(16)
                        if (value > 10) return@collect
                        viewModel.homeData.rate = value
                        setRate()
                        binding.progressHz.setScale(viewModel.homeData.rate * 4)
                    }
                    if (it.contains(ISSUES_PULSE_XXXX)) {
//                        val value = it.substring(it.length - 8).toLong(16)
//                        viewModel.homeData.pulse = value
                        delay(90)
                        binding.tvPulse.text = getString(R.string.pulse, viewModel.homeData.pulse)
                    }
                    if (it.contains(ISSUED_SPOT_XX)) {
                        val value = it.substring(it.length - 4).toInt(16)
                        if (value < 2 || value > 10) return@collect
                        viewModel.homeData.spot = value
                        binding.tvSpot.text = getString(R.string.spot, value)
                    }
                    if (it.contains(ISSUED_RED_LIGHT_XX)) {
                        val value = it.substring(it.length - 4).toInt(16)
                        if (value < 0 || value > 10) return@collect
                        viewModel.homeData.readLight = value
                        binding.seekRedLight.progress = value
                    }
                }
            }
        }
    }

    private fun startState(it: Boolean) {
        binding.toggleSwitch.load(if (it) R.drawable.ic_start else R.drawable.ic_stop)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClick() {
//        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
//            if (isChecked) {
//                viewModel.homeData.waveId = checkedId
//                when (checkedId) {
//                    R.id.btn_1064 -> viewModel.sendHex(UPLOAD_1064)
//                    R.id.btn_523 -> viewModel.sendHex(UPLOAD_532)
//                }
//            }
//        }

        binding.toggleGroup.setOnClickListener {
            viewModel.homeData.waveId.also {
                if (it) viewModel.sendHex(UPLOAD_1064) else viewModel.sendHex(UPLOAD_532)
                viewModel.storeWave(!it)
            }
        }
        binding.btnEnergyPlus.setOnClickListener {
            if (viewModel.homeData.energy >= 10) return@setOnClickListener
            viewModel.senBtnEvent(BtnType.Energy(false))
            viewModel.sendHex(UPLOAD_ENERGY_PLUS)
            ++viewModel.homeData.energy
            setEnergy()
            binding.progressEnergy.setScale(viewModel.homeData.energy * 4)
        }
        binding.btnEnergyMinus.setOnClickListener {
            if (viewModel.homeData.energy <= 1) return@setOnClickListener
            viewModel.senBtnEvent(BtnType.Energy(true))
            viewModel.sendHex(UPLOAD_ENERGY_MINUS)
            --viewModel.homeData.energy
            setEnergy()
            binding.progressEnergy.setScale((viewModel.homeData.energy * 4) - 1)
        }
        binding.btnRatePlus.setOnClickListener {
            if (viewModel.homeData.rate >= 10) return@setOnClickListener
            viewModel.senBtnEvent(BtnType.Rate(false))
            (++viewModel.homeData.rate).also {
                viewModel.sendHex(UPLOAD_RATE_XX + it.getHexString())
            }
            setRate()
            binding.progressHz.setScale(viewModel.homeData.rate * 4)
        }
        binding.btnRateMinus.setOnClickListener {
            if (viewModel.homeData.rate <= 1) return@setOnClickListener
            viewModel.senBtnEvent(BtnType.Rate(true))
            (--viewModel.homeData.rate).also {
                viewModel.sendHex(UPLOAD_RATE_XX + it.getHexString())
            }
            setRate()
            binding.progressHz.setScale((viewModel.homeData.rate * 4) - 1)
        }
//        binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
//            viewModel.homeData.switch = isChecked
//            if (viewModel.homeData.switch) viewModel.sendHex(UPLOAD_ON)
//            else viewModel.sendHex(UPLOAD_OFF)
//        }
        binding.toggleSwitch.setOnClickListener {
            viewModel.homeData.switch.also {
                viewModel.senBtnEvent(BtnType.Start(!it))
                "start Click ${!it}".logCat()
                if (!it) viewModel.sendHex(UPLOAD_ON) else viewModel.sendHex(UPLOAD_OFF)
            }
        }
        binding.btnFlash.setOnClickListener { viewModel.sendHex(UPLOAD_FLASH) }
        binding.btnAod.setOnClickListener { viewModel.sendHex(UPLOAD_AOD) }
        binding.btnOff.setOnClickListener { viewModel.sendHex(UPLOAD_CLOSE) }
//        binding.seekRedLight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(p0: SeekBar?, i: Int, p2: Boolean) {}
//            override fun onStartTrackingTouch(p0: SeekBar?) {}
//            override fun onStopTrackingTouch(v: SeekBar?) {
//                v?.progress?.let {
//                    viewModel.homeData.readLight = it
//                    viewModel.sendHex(UPLOAD_RED_LIGHT_XX + it.getHexString())
//                }
//
//            }
//        })
        binding.seekRedLight.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }
        binding.imgRedlightPlus.setOnClickListener {
            if (viewModel.homeData.readLight >= 10) return@setOnClickListener
            (++viewModel.homeData.readLight).also {
                viewModel.sendHex(UPLOAD_RED_LIGHT_XX + it.getHexString())
                binding.seekRedLight.progress = it
            }
        }
        binding.imgReadlightMinus.setOnClickListener {
            if (viewModel.homeData.readLight <= 0) return@setOnClickListener
            (--viewModel.homeData.readLight).also {
                viewModel.sendHex(UPLOAD_RED_LIGHT_XX + it.getHexString())
                binding.seekRedLight.progress = it
            }
        }
    }

    private fun initUIState() {
        setEnergy()
        setRate()
        binding.progressEnergy.setScale(viewModel.homeData.energy * 4)
        binding.progressHz.setScale(viewModel.homeData.rate * 4)
        binding.tvPulse.text = getString(R.string.pulse, viewModel.homeData.pulse)
        binding.tvSpot.text = getString(R.string.spot, viewModel.homeData.spot)
       // binding.toggleSwitch.isChecked = viewModel.homeData.switch
        binding.seekRedLight.progress = viewModel.homeData.readLight
        energyBtnState(viewModel.homeData.energyBtn)
        rateBtnState(viewModel.homeData.rateBtn)
        startState(viewModel.homeData.switch)
      //  viewModel.homeData.waveId?.let { binding.toggleGroup.check(it) }
    }

    private fun rateBtnState(it: Boolean) {
        binding.btnRateMinus.load(if (it) R.drawable.ic_minus_checked else R.drawable.ic_minus_uncheck)
        binding.btnRatePlus.load(if (!it) R.drawable.ic_plus_checked else R.drawable.ic_plus_uncheck)
    }

    private fun energyBtnState(it: Boolean) {
        binding.btnEnergyMinus.load(if (it) R.drawable.ic_minus_checked else R.drawable.ic_minus_uncheck)
        binding.btnEnergyPlus.load(if (!it) R.drawable.ic_plus_checked else R.drawable.ic_plus_uncheck)
    }

    private fun setRate() {
        binding.tvHz.text = getString(R.string.hz, viewModel.homeData.rate)  //binding.tvHz.text
        binding.progressHz.setProgress(viewModel.homeData.rate * 10)
    }

    private fun setEnergy() {
        binding.tvEnergy.text = getString(R.string.energy, viewModel.homeData.energy)
      //  binding.progressEnergy.setText(getString(R.string.energy, viewModel.homeData.energy))
        binding.progressEnergy.setProgress(viewModel.homeData.energy * 10)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        "HomeDestroy".logCat()
    }

}