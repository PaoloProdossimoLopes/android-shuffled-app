package com.programou.shuffled

import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.programou.shuffled.databinding.FragmentDeckListBinding
import com.programou.shuffled.databinding.FragmentResultBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Serializable
import java.text.DecimalFormat

data class ResultViewData(val deckTitle: String, val totalOfCards: Int, val numberOfEasy: Int, val numberOfMid: Int, val numberOfHard: Int): Serializable

class ResultFragment: Fragment(R.layout.fragment_result) {

    private lateinit var binding: FragmentResultBinding

    private val arguments: ResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentResultBinding.bind(view)

        binding.tvCongratulationDeckName.text = arguments.viewData.deckTitle

        val badPercentage = (arguments.viewData.numberOfHard.toDouble() * 100.0) / arguments.viewData.totalOfCards.toDouble()
        val easyPercentage = (arguments.viewData.numberOfEasy.toDouble() * 100.0) / arguments.viewData.totalOfCards.toDouble()
        val midPercentage = (arguments.viewData.numberOfMid.toDouble() * 100.0) / arguments.viewData.totalOfCards.toDouble()

        binding.tvBadPercentage.text = (String.format("%.2f", badPercentage) + "%").replace(".", ",")

        binding.tvEasyPercentage.text = (String.format("%.2f", easyPercentage) + "%").replace(".", ",")

        binding.tvSkipPercentage.text = (String.format("%.2f", midPercentage) + "%").replace(".", ",")

        binding.buttonDone.setOnClickListener {
            findNavController().popBackStack(R.id.deckFragment, false)
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            delay(300)

            val badPercentage = (arguments.viewData.numberOfHard.toDouble() * 100.0) / arguments.viewData.totalOfCards.toDouble()
            val easyPercentage = (arguments.viewData.numberOfEasy.toDouble() * 100.0) / arguments.viewData.totalOfCards.toDouble()
            val midPercentage = (arguments.viewData.numberOfMid.toDouble() * 100.0) / arguments.viewData.totalOfCards.toDouble()

            binding.circularProgressBad.setProgress(badPercentage.toInt(), true)
            binding.circularProgressEasy.setProgress(easyPercentage.toInt(), true)
            binding.circularProgressSkip.setProgress(midPercentage.toInt(), true)
        }
    }
}