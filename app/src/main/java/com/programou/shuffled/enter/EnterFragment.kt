package com.programou.shuffled.enter

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.programou.shuffled.R
import com.programou.shuffled.databinding.FragmentEnterBinding
import com.programou.shuffled.utils.hideKeyboard

class EnterFragment: Fragment(R.layout.fragment_enter) {
    private lateinit var binding: FragmentEnterBinding
//    private val viewModel: EnterViewModel by lazy {
//        val enterUseCase = EnterAccountUseCase()
//        EnterViewModel(enterUseCase)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEnterBinding.bind(view)

        configureActions()
    }

    private fun configureActions() {
        with (binding) {
            buttonEnter.setOnClickListener { onEnterActionHandler(it) }
            cardGoogle.setOnClickListener { onGoogleActionHandler() }
            buttonDontHaveAccount.setOnClickListener { onCreateAccountActionHandler() }
            binding.root.setOnClickListener { hideKeyboard() }
        }
    }

    private fun onEnterActionHandler(view: View) {
        hideKeyboard()
    }

    private fun onGoogleActionHandler() = Log.i("::DEBUG::", "enter with google")

    private fun onCreateAccountActionHandler() {
        val action = EnterFragmentDirections.actionEnterFragmentToRegisterFragment()
        findNavController().navigate(action)
    }
}
