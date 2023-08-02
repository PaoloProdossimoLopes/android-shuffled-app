package com.programou.shuffled.authenticated.flashcard

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewData
import com.programou.shuffled.authenticated.ListAdapter
import com.programou.shuffled.authenticated.result.ResultViewData
import com.programou.shuffled.databinding.FragmentFlashCardBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FlashCardFragment: Fragment(R.layout.fragment_flash_card), View.OnClickListener {

    private lateinit var binding: FragmentFlashCardBinding
    private val arguments: FlashCardFragmentArgs by navArgs()
    private val flashcardListAdapter = ListAdapter<FlashCardViewData>()
    private val viewModel: FlashcardViewModel by viewModels {
        FlashcardViewModel.Facotry(arguments.deck, LocalFlashcardRepository(requireContext()))
    }

    private var isScrollEnabled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFlashCardBinding.bind(view)

        onViewCreated()
    }

    private fun onViewCreated() {
        registerFlashcardItemViewHolder()
        setupFlashcardRecyclerView()
        setupLayout()
        setupClickListeners()
        setupObservers()

        viewModel.presentCards()
    }

    private fun setupObservers() {
        viewModel.onItemsChange.observe(requireActivity()) { viewDatas ->
            onItemChangeWith(viewDatas.shuffled())
        }

        viewModel.onEasySelectChange.observe(requireActivity()) {
            setupEasyImageViewActive()
        }

        viewModel.onIntermediateSelect.observe(requireActivity()) {
            setupIntermediateImageViewActive()
        }

        viewModel.onHardSelectChange.observe(requireActivity()) {
            setupHardImageViewActive()
        }

        viewModel.onDisableButtonsChange.observe(requireActivity()) {
            onDisableButtons()
        }

        viewModel.onUpdateStepChange.observe(requireActivity()) { newState ->
            onUpdateStep(newState)
        }

        viewModel.onNavigateToResult.observe(requireActivity()) { flashcardResult ->
            onNavigateToResult(flashcardResult)
        }
    }

    private fun setupEasyImageViewActive() {
        binding.easyImageViewInFlashcardFragment.setImageDrawable(getDrawableBy(R.drawable.ic_happy_emoji_active))
    }

    private fun setupIntermediateImageViewActive() {
        binding.intermediateImageViewInFlashcardFragment.setImageDrawable(getDrawableBy(R.drawable.ic_normal_emoji_active))
    }

    private fun setupHardImageViewActive() {
        binding.hardImageViewInFlashcardFragment.setImageDrawable(getDrawableBy(R.drawable.ic_bad_emoji_active))
    }

    private fun onItemChangeWith(viewDatas: List<FlashCardViewData>) {
        val items = viewDatas.map {
            ItemViewData(FlashCardItemViewHolder.IDENTIFIER, it)
        }
        flashcardListAdapter.update(items)
    }

    private fun onNavigateToResult(flashcardResult: FlashcardResult) {
        val viewData = ResultViewData(
            arguments.deck.id, flashcardResult.deckTitle, flashcardResult.totalOfCards,
            flashcardResult.numberOfEasy, flashcardResult.numberOfMid,
            flashcardResult.numberOfHard
        )
        val action = FlashCardFragmentDirections.actionFlashCardFragmentToResultFragment(viewData)
        findNavController().navigate(action)
    }

    private fun onUpdateStep(newState: FlashcardStep) {
        binding.studyProgressLinearProgressIndicatorInFlashcardFragment.setProgress(
            newState.progress,
            true
        )
        binding.currentCardStepTextViewInFlashcardFragment.text = newState.step
        binding.flashcardRecyclerViewInFlashcardFragment.smoothSnapToPosition(newState.cardPosition)
    }

    private fun onDisableButtons() {
        binding.easyImageViewInFlashcardFragment.isClickable = false
        binding.hardImageViewInFlashcardFragment.isClickable = false
        binding.intermediateImageViewInFlashcardFragment.isClickable = false

        lifecycleScope.launch {
            delay(400)

            disableListScrolling()
            binding.easyImageViewInFlashcardFragment.setImageDrawable(getDrawableBy(R.drawable.ic_happy_emoji_deactive))
            binding.hardImageViewInFlashcardFragment.setImageDrawable(getDrawableBy(R.drawable.ic_bad_emoji_deactive))
            binding.intermediateImageViewInFlashcardFragment.setImageDrawable(getDrawableBy(R.drawable.ic_normal_emoji_deactive))
        }
    }

    private fun setupClickListeners() {
        binding.easyImageContainerCardViewInFlashcardFragment.setOnClickListener(this)
        binding.intermediateImageContainerCardViewInFlashcardFragment.setOnClickListener(this)
        binding.hardImageContainerCardViewInFlashcardFragment.setOnClickListener(this)
        binding.backArrowIndicatorImageViewInFlashcardFragment.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            binding.easyImageContainerCardViewInFlashcardFragment -> selectEasy()
            binding.intermediateImageContainerCardViewInFlashcardFragment -> selectIntermediate()
            binding.hardImageContainerCardViewInFlashcardFragment -> selectHard()
            binding.backArrowIndicatorImageViewInFlashcardFragment -> {
                presentInteruptStudy()
            }
        }
    }

    private fun presentInteruptStudy() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.textAreYouSureInteruptStudyInFlashFragment))
            .setMessage(getString(R.string.textYourProgressWillBeLostInFlashFragment))
            .setPositiveButton(getString(R.string.textNo), null)
            .setNegativeButton(getString(R.string.textYes)) { _, _ ->
                val action = FlashCardFragmentDirections.actionFlashCardFragmentToDeckFragment(
                    arguments.deck.id
                )
                findNavController().navigate(action)
            }
            .show()
    }

    private fun selectEasy() {
        enableListScrooling()
        viewModel.selectEasy()
    }

    private fun selectIntermediate() {
        enableListScrooling()
        viewModel.selectIntermediate()
    }

    private fun selectHard() {
        enableListScrooling()
        viewModel.selectHard()
    }

    private fun enableListScrooling() {
        isScrollEnabled = true
    }

    private fun disableListScrolling() {
        isScrollEnabled = false
    }

    private fun registerFlashcardItemViewHolder() {
        flashcardListAdapter.register(FlashCardItemViewHolder.IDENTIFIER) { parent ->
            FlashCardItemViewHolder.instantiate(parent)
        }
    }

    private fun setupFlashcardRecyclerView() {
        binding.flashcardRecyclerViewInFlashcardFragment.adapter = flashcardListAdapter
        binding.flashcardRecyclerViewInFlashcardFragment.layoutManager = LinearScrollLayoutHandler(requireContext(), canScroll = {
            isScrollEnabled
        })
    }

    private fun setupLayout() {
        binding.deckTitleTextViewInFlashcardFragment.text = arguments.deck.name
        binding.totalOfCardsTextViewInFlashcardFragment.text = viewModel.getCards().count().toString()
        binding.currentCardStepTextViewInFlashcardFragment.text = "1"
        binding.studyProgressLinearProgressIndicatorInFlashcardFragment.setProgress(viewModel.getProgress(), true)
    }

    private fun getDrawableBy(id: Int) = requireActivity().getDrawable(id)

    class LinearScrollLayoutHandler(context: Context, private val canScroll: () -> Boolean): LinearLayoutManager(context, HORIZONTAL, false) {
        override fun canScrollHorizontally() = canScroll()
    }
}