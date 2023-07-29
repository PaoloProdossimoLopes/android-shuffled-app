package com.programou.shuffled.authenticated.result

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.progressindicator.CircularProgressIndicator
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
        textViewPercentage.text = usage.percentage
        textViewDescription.text = usage.description
    }

    private fun configureCongratulationSectionFrom(congratulation: ResultViewModel.Response.Congratulation) {
        binding.congratulationTitleTextView.text = congratulation.title
        binding.congratulationDescriptionTextView.text = congratulation.description
        binding.congratulationDeckNameTextView.text = congratulation.name
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            delay(300) //0.3s

            val request = ResultViewModel.Request(arguments.viewData)
            configureProgressForCircularProgressIndicators(request)
        }
    }

    private fun configureProgressForCircularProgressIndicators(request: ResultViewModel.Request) {
        configureProgressForCircularProgressView(binding.hardCircularProgressView, request.viewData.numberOfHard, request.viewData.totalOfCards)
        configureProgressForCircularProgressView(binding.intermediateCircularProgressView, request.viewData.numberOfMid, request.viewData.totalOfCards)
        configureProgressForCircularProgressView(binding.easyCircularProgressView, request.viewData.numberOfEasy, request.viewData.totalOfCards)
    }

    private fun configureProgressForCircularProgressView(progress: CircularProgressIndicator, cards: Int, totalOfCards: Int) {
        val percentage = viewModel.calculatePercentageOf(cards, totalOfCards)
        progress.setProgress(percentage.toInt(), true)
    }
}
