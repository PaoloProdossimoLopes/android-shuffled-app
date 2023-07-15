package com.programou.shuffled.unauthenticated.enter

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.programou.shuffled.FirebaseAuthClientProviderAdapter
import com.programou.shuffled.GoogleAuthenticatorProvider
import com.programou.shuffled.R
import com.programou.shuffled.databinding.FragmentEnterBinding
import com.programou.shuffled.utils.hideKeyboard

class EnterFragment : Fragment(R.layout.fragment_enter) {

    private lateinit var binding: FragmentEnterBinding

    private val firebaseProvider = FirebaseAuthClientProviderAdapter.shared
    private var googleAuthentication: GoogleAuthenticatorProvider? = null
    private val viewModel: EnterViewModel by lazy {
        val repository = RemoteEnterRepository(firebaseProvider)
        val useCase = EnterAccountUseCase(repository)
        EnterViewModel(useCase)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEnterBinding.bind(view)

        googleAuthentication = GoogleAuthenticatorProvider(this)

        configureActions()
        configureFieldsObserver()
        disableEnterButton()

        viewModel.errorMessage.observe(requireActivity()) { errorViewData ->
            Snackbar.make(binding.root, errorViewData.message, Snackbar.LENGTH_LONG).show()
            binding.progressEnterButtonLoader.isVisible = false
            binding.buttonEnter.text = "Entrar"
        }

        viewModel.user.observe(requireActivity()) { user ->
            //TODO: Navigate to Home
        }

        firebaseProvider.logout()
        googleAuthentication?.logout()
    }

    private fun configureActions() {
        with(binding) {
            buttonEnter.setOnClickListener { onEnterActionHandler(it) }
            cardGoogle.setOnClickListener { onGoogleActionHandler() }
            buttonDontHaveAccount.setOnClickListener { onCreateAccountActionHandler() }
            binding.root.setOnClickListener { hideKeyboard() }
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
        binding.buttonEnter.isEnabled = true
        binding.buttonEnter.isActivated = true
        binding.buttonEnter.isClickable = true
    }

    private fun disableEnterButton() {
        binding.buttonEnter.isEnabled = false
        binding.buttonEnter.isActivated = false
        binding.buttonEnter.isClickable = false
    }

    private fun onEnterActionHandler(view: View) {
        hideKeyboard()
        binding.progressEnterButtonLoader.isVisible = true
        binding.buttonEnter.text = String()

        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.toString()
        val userViewData = UserViewData(email, password)
        viewModel.enter(userViewData)
    }

    private fun onGoogleActionHandler() {
        binding.progressGoogleButtonLoader.isVisible = true
        binding.linearGoogleButtonContainer.visibility = View.GONE

        googleAuthentication?.presentAuthentication { credentialResult ->
            credentialResult.exceptionOrNull()?.let { e ->
                val message = e.message ?: "Error"
                val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                return@presentAuthentication snackbar.show()
            }

            credentialResult.getOrNull()?.let { credential ->
                firebaseProvider.auth(credential) { authenticationResult ->
                    authenticationResult.exceptionOrNull()?.let { e ->
                        val message = e.message ?: "Error"
                        Log.i("DEBUG ERROR:", e.message!!)
                        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                        return@auth snackbar.show()
                    }

                    authenticationResult.getOrNull()?.let { user ->
                        val toast = Toast.makeText(requireContext(), "Autenticado", Toast.LENGTH_LONG)
                        return@auth toast.show()
                    }
                }
            }

            binding.progressGoogleButtonLoader.isVisible = false
            binding.linearGoogleButtonContainer.visibility = View.VISIBLE
        }
    }

    private fun onCreateAccountActionHandler() {
        val action = EnterFragmentDirections.actionEnterFragmentToRegisterFragment()
        findNavController().navigate(action)
    }
}