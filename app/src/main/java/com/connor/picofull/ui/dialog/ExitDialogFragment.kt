package com.connor.picofull.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.connor.picofull.R
import com.connor.picofull.databinding.DialogExitBinding
import com.connor.picofull.utils.showToast

class ExitDialogFragment : DialogFragment() {

    private var _binding: DialogExitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogExitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnExit.setOnClickListener {
            if (binding.editInputPass.text.toString() == "123456") {
                activity?.finish()
            } else getString(R.string.password_err).showToast()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}