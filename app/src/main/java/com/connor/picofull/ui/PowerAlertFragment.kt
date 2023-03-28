package com.connor.picofull.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.connor.picofull.R
import com.connor.picofull.databinding.FragmentAlertBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PowerAlertFragment : Fragment() {


    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        binding.tvAlert.text = getString(R.string.power_alert)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}