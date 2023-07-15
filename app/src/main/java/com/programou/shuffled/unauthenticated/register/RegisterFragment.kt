package com.programou.shuffled.unauthenticated.register

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.programou.shuffled.FirebaseAuthClientProviderAdapter
import com.programou.shuffled.R
import com.programou.shuffled.databinding.FragmentRegisterBinding
import com.programou.shuffled.unauthenticated.enter.UserViewData
import com.programou.shuffled.utils.hideKeyboard

class RegisterFragment: Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by lazy {
        val repository = RemoteRegisterRepository(FirebaseAuthClientProviderAdapter.shared)
        val usecase = RegisterAccountUseCase(repository)
        RegisterViewModel(usecase)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegisterBinding.bind(view)

        binding.imageBackArrow.setOnClickListener { popFragment() }
        binding.buttonAlreadyHaveAccount.setOnClickListener { popFragment() }
        binding.root.setOnClickListener { hideKeyboard() }
        binding.buttonRegister.setOnClickListener { onEnterActionHandler() }

        configureFieldsObserver()
        onButtonStateChange()

        viewModel.errorMessage.observe(requireActivity()) { errorViewData ->
            binding.progressRegisterButtonLoader.isVisible = false
            binding.buttonRegister.text = "Registrar"
            binding.editEmail.error = errorViewData.message
            binding.editPassword.error = errorViewData.message
        }

        viewModel.user.observe(requireActivity()) { user ->
            //TODO: Navigate to Home
        }
    }

    private fun configureFieldsObserver() {
        binding.editEmail.addTextChangedListener(onTextChanged = { _, _, _, _ -> onButtonStateChange() })
        binding.editPassword.addTextChangedListener(onTextChanged = { _, _, _, _ -> onButtonStateChange() })
    }

    private fun onButtonStateChange() {
        val emailIsValid = binding.editEmail.text.isNotEmpty()
        val passwordIsValid = binding.editPassword.text.isNotEmpty()

        val isValid = emailIsValid && passwordIsValid

        if (isValid) { enableEnterButton() } else { disableEnterButton() }
    }

    private fun enableEnterButton() {
        binding.buttonRegister.isEnabled = true
        binding.buttonRegister.isActivated = true
        binding.buttonRegister.isClickable = true
    }

    private fun disableEnterButton() {
        binding.buttonRegister.isEnabled = false
        binding.buttonRegister.isActivated = false
        binding.buttonRegister.isClickable = false
    }

    private fun onEnterActionHandler() {
        hideKeyboard()
        binding.progressRegisterButtonLoader.isVisible = true
        binding.buttonRegister.text = String()

        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.toString()
        val userViewData = UserViewData(email, password)
        viewModel.register(userViewData)
    }

    private fun popFragment() {
        findNavController().popBackStack()
    }
}