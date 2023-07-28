package com.programou.shuffled.authenticated.result

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.programou.shuffled.R
import com.programou.shuffled.databinding.FragmentResultBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ResultFragment: Fragment(R.layout.fragment_result) {

    private lateinit var binding: FragmentResultBinding

    private val arguments: ResultFragmentArgs by navArgs()
    private val viewModel: ResultViewModel by lazy {
        ResultViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentResultBinding.bind(view)

        val request = ResultViewModel.Request(arguments.viewData)
        val response = viewModel.configure(request)
        configureLayout(response)
    }

    private fun configureLayout(response: ResultViewModel.Response) {
        configureCongratulationSectionFrom(response.congratulation)

        configureUsageFor(binding.hardPercentageTextView, binding.hardDescriptionTextView, response.usage.hard)
        configureUsageFor(binding.easyPercentageTextView, binding.easyDescriptionTextView, response.usage.easy)
        configureUsageFor(binding.intermediatePercentageTextView, binding.intermediateDescriptionTextView, response.usage.intermediate)
        binding.completedMessageTextView.text = response.message

        configureFinishButtonAction()
    }

    private fun configureFinishButtonAction() {
        binding.doneButton.setOnClickListener {
            findNavController().popBackStack(R.id.deckFragment, false)
        }
    }

    private fun configureUsageFor(textViewPercentage: TextView, textViewDescription: TextView, usage: ResultViewModel.Response.Use) {
        with (usage) {
            textViewPercentage.text = percentage
            textViewDescription.text = description
        }
    }

    private fun configureCongratulationSectionFrom(congratulation: ResultViewModel.Response.Congratulation) {
        with (congratulation) {
            binding.congratulationTitleTextView.text = title
            binding.congratulationDescriptionTextView.text = description
            binding.congratulationDeckNameTextView.text = name
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            delay(300) //0.3s

            val request = ResultViewModel.Request(arguments.viewData)

            configureCircularProgress(request)
        }
    }

    private fun configureCircularProgress(request: ResultViewModel.Request) {
        configureHardCircularProgress(request)
        configureEasyCircularProgress(request)
        configrueIntermediateCircularProgress(request)
    }

    private fun configrueIntermediateCircularProgress(request: ResultViewModel.Request) {
        val intermediatePercentage = viewModel.getPercentageOfIntermediate(request)
        binding.intermediateCircularProgressView.setProgress(intermediatePercentage.toInt(), true)
    }

    private fun configureEasyCircularProgress(request: ResultViewModel.Request) {
        val easyPercentage = viewModel.getPercentageOfEasy(request)
        binding.easyCircularProgressView.setProgress(easyPercentage.toInt(), true)
    }

    private fun configureHardCircularProgress(request: ResultViewModel.Request) {
        val badPercentage = viewModel.getPercentageOfHard(request)
        binding.hardCircularProgressView.setProgress(badPercentage.toInt(), true)
    }
}
