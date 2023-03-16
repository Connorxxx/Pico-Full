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
import com.connor.picofull.constant.ISSUED_BUZZ_OFF
import com.connor.picofull.constant.ISSUED_BUZZ_ON
import com.connor.picofull.constant.UPLOAD_BUZZ_OFF
import com.connor.picofull.constant.UPLOAD_BUZZ_ON
import com.connor.picofull.databinding.FragmentHomeBinding
import com.connor.picofull.databinding.FragmentSettingsBinding
import com.connor.picofull.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.rgBuzz.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                R.id.radio_buzz_off -> viewModel.sendHex(UPLOAD_BUZZ_OFF)
                R.id.radio_buzz_on -> viewModel.sendHex(UPLOAD_BUZZ_ON)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.receiveEvent.collect {
                        when (it) {
                            ISSUED_BUZZ_OFF -> binding.radioBuzzOff.isChecked = true
                            ISSUED_BUZZ_ON -> binding.radioBuzzOn.isChecked = true
                        }
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