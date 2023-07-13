package com.programou.shuffled.enter

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.programou.shuffled.R
import com.programou.shuffled.databinding.FragmentEnterBinding

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
            buttonEnter.setOnClickListener { onEnterActionHandler() }
            cardGoogle.setOnClickListener { onGoogleActionHandler() }
            buttonDontHaveAccount.setOnClickListener { onCreateAccountActionHandler() }
        }
    }

    private fun onEnterActionHandler() = Log.i("::DEBUG::", "enter was click")

    private fun onGoogleActionHandler() = Log.i("::DEBUG::", "enter with google")

    private fun onCreateAccountActionHandler() = Log.i("::DEBUG::", "register was clicked")
}