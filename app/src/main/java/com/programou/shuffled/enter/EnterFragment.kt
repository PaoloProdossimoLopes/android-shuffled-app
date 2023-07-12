package com.programou.shuffled.enter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.programou.shuffled.R
import com.programou.shuffled.databinding.FragmentEnterBinding

class EnterFragment: Fragment(R.layout.fragment_enter) {
    private lateinit var binding: FragmentEnterBinding

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

    private fun onEnterActionHandler() {}

    private fun onGoogleActionHandler() {}

    private fun onCreateAccountActionHandler() {}
}