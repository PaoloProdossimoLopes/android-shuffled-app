package com.programou.shuffled.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.programou.shuffled.R
import com.programou.shuffled.databinding.FragmentRegisterBinding
import com.programou.shuffled.utils.hideKeyboard

class RegisterFragment: Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegisterBinding.bind(view)

        binding.imageBackArrow.setOnClickListener { popFragment() }
        binding.buttonAlreadyHaveAccount.setOnClickListener { popFragment() }
        binding.root.setOnClickListener { hideKeyboard() }
        binding.buttonEnter.setOnClickListener { hideKeyboard() }
    }

    private fun popFragment() {
        findNavController().popBackStack()
    }

}