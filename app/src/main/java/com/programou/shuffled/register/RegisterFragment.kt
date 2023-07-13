package com.programou.shuffled.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.programou.shuffled.R
import com.programou.shuffled.databinding.FragmentRegisterBinding

class RegisterFragment: Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegisterBinding.bind(view)

        binding.imageBackArrow.setOnClickListener { popFragment() }
        binding.buttonAlreadyHaveAccount.setOnClickListener { popFragment() }
    }

    private fun popFragment() {
        findNavController().popBackStack()
    }

}