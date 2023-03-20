package com.connor.picofull.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.connor.picofull.constant.UPLOAD_INPUT_XXXX
import com.connor.picofull.databinding.FragmentInputBinding
import com.connor.picofull.utils.getHexString
import com.connor.picofull.utils.logCat
import com.connor.picofull.utils.showToast
import com.connor.picofull.viewmodels.MainViewModel

class InputFragment : Fragment() {

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    private val imm by lazy { context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        binding.editPassword.post {
            with(binding.editPassword) {
                requestFocus()
                imm.showSoftInput(this, 0)
                setOnEditorActionListener { textView, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE && textView.text.length >= 4) {  //后台
                        text.toString().getHexString().logCat()
                        viewModel.sendHex(UPLOAD_INPUT_XXXX + text.toString().getHexString())
                        imm.hideSoftInputFromWindow(windowToken, 0)
                    } else "please input 4 digit password".showToast()
                    return@setOnEditorActionListener true
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