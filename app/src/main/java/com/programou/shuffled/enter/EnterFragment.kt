package com.programou.shuffled.enter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.programou.shuffled.R
import com.programou.shuffled.databinding.FragmentEnterBinding

class EnterFragment: Fragment(R.layout.fragment_enter), View.OnClickListener {
    private lateinit var binding: FragmentEnterBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEnterBinding.bind(view)

        configureActions()
    }

    private fun configureActions() {
        binding.buttonEnter.setOnClickListener(this)
        binding.cardGoogle.setOnClickListener(this)
        binding.buttonDontHaveAccount.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            binding.buttonEnter -> onEnterActionHandler()
            binding.cardGoogle -> onGoogleActionHandler()
            binding.buttonDontHaveAccount -> onCreateAccountActionHandler()

            else -> return
        }
    }

    private fun onEnterActionHandler() {}

    private fun onGoogleActionHandler() {}

    private fun onCreateAccountActionHandler() {}
}