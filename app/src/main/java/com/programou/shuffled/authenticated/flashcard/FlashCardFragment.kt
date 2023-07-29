package com.programou.shuffled.authenticated.flashcard

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewData
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.authenticated.ListAdapter
import com.programou.shuffled.authenticated.result.ResultViewData
import com.programou.shuffled.databinding.FragmentFlashCardBinding
import com.programou.shuffled.databinding.ViewFlashCardItemBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FlashCardFragment : Fragment(R.layout.fragment_flash_card) {
    private lateinit var binding: FragmentFlashCardBinding
    private val arguments: FlashCardFragmentArgs by navArgs()
    private val flashcardListAdapter = ListAdapter<FlashCardViewData>()
    private var isScrollEnabled = false
    private var currentItem = 0

    private var numberOfCardsDifficulty = 0
    private var numberOfCardsMid = 0
    private var numberOfCardsEasy = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFlashCardBinding.bind(view)

        binding.deckTitleTextView.text = arguments.deck.name

        flashcardListAdapter.register(FlashCardItemViewHolder.IDENTIFIER) { parent ->
            FlashCardItemViewHolder.instantiate(parent)
        }

        binding.recyclerFlahscards.adapter = flashcardListAdapter
        binding.recyclerFlahscards.layoutManager = LinearScrollLayoutHandler(requireContext(), canScroll = {
            isScrollEnabled
        })

        binding.mcvGood.setOnClickListener {
            numberOfCardsEasy++

            binding.imageGood.setImageDrawable(getDrawableBy(R.drawable.ic_happy_emoji_active))
            binding.imageBad.setImageDrawable(getDrawableBy(R.drawable.ic_bad_emoji_deactive))
            binding.imageSkip.setImageDrawable(getDrawableBy(R.drawable.ic_normal_emoji_deactive))

            goToNextItem()
        }

        binding.mcvSkip.setOnClickListener {
            numberOfCardsMid++

            binding.imageGood.setImageDrawable(getDrawableBy(R.drawable.ic_happy_emoji_deactive))
            binding.imageBad.setImageDrawable(getDrawableBy(R.drawable.ic_bad_emoji_deactive))
            binding.imageSkip.setImageDrawable(getDrawableBy(R.drawable.ic_normal_emoji_active))

            goToNextItem()
        }

        binding.tvTotalCards.text = arguments.deck.cards.count().toString()

        binding.mcvBad.setOnClickListener {
            numberOfCardsDifficulty++

            binding.imageGood.setImageDrawable(getDrawableBy(R.drawable.ic_happy_emoji_deactive))
            binding.imageBad.setImageDrawable(getDrawableBy(R.drawable.ic_bad_emoji_active))
            binding.imageSkip.setImageDrawable(getDrawableBy(R.drawable.ic_normal_emoji_deactive))

            goToNextItem()
        }

        binding.backArrowIndicatorImageViewInFlashcardFragment.setOnClickListener {
            findNavController().popBackStack()
        }

        val cards = arguments.deck.cards.map {
            ItemViewData(FlashCardItemViewHolder.IDENTIFIER, FlashCardViewData(it.id!!, it.question, it.awnser))
        }
        flashcardListAdapter.update(cards)
    }

    private fun disableAllButtons() {
        binding.imageGood.setImageDrawable(getDrawableBy(R.drawable.ic_happy_emoji_deactive))
        binding.imageBad.setImageDrawable(getDrawableBy(R.drawable.ic_bad_emoji_deactive))
        binding.imageSkip.setImageDrawable(getDrawableBy(R.drawable.ic_normal_emoji_deactive))
    }

    private fun getDrawableBy(id: Int) = requireActivity().getDrawable(id)

    private fun goToNextItem() {
        if (currentItem < arguments.deck.cards.count() - 1) {
            lifecycleScope.launch {
                isScrollEnabled = true
                delay(500)
                currentItem++

                val totalOfCards = arguments.deck.cards.count()
                val currentPosition = (currentItem + 1)
                val progress = ((currentPosition * 100) / totalOfCards)

                binding.tvCurrentCardNumber.text = currentPosition.toString()
                binding.lpProgress.setProgress(progress, true)
                binding.recyclerFlahscards.smoothSnapToPosition(currentItem)

                disableAllButtons()

                delay(100)
                isScrollEnabled = false
            }
        } else {
            val viewData = ResultViewData(arguments.deck.name, arguments.deck.cards.count(), numberOfCardsEasy, numberOfCardsMid, numberOfCardsDifficulty)
            val action = FlashCardFragmentDirections.actionFlashCardFragmentToResultFragment(viewData)
            findNavController().navigate(action)
        }
    }
    class LinearScrollLayoutHandler(context: Context, private val canScroll: () -> Boolean): LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
        override fun canScrollHorizontally() = canScroll()
    }

}
class FlashCardViewData(val id: Int, val question: String, val answer: String)

class FlashCardItemViewHolder private constructor(private val binding: ViewFlashCardItemBinding): ItemViewHolder<FlashCardViewData>(binding.root) {

    companion object {
        val IDENTIFIER: Int by lazy { FlashCardItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup): FlashCardItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewFlashCardItemBinding.inflate(inflater, parent, false)

            return FlashCardItemViewHolder(binding)
        }
    }

    override fun bind(viewData: FlashCardViewData) {
        binding.tvCardContent.text = viewData.question
        binding.tvCardContent.typeface = Typeface.DEFAULT_BOLD

        binding.imgFlipIndicator.setOnClickListener {
            val oa1 = ObjectAnimator.ofFloat(binding.cardContainer, "scaleX", 1f, 0f)
            val oa2 = ObjectAnimator.ofFloat(binding.cardContainer, "scaleX", 0f, 1f)

            oa1.interpolator = DecelerateInterpolator()
            oa2.interpolator = AccelerateInterpolator()

            oa1.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    if (binding.tvCardContent.text == viewData.question) {
                        binding.tvCardContent.text = viewData.answer
                        binding.tvCardContent.typeface = Typeface.DEFAULT
                    } else {
                        binding.tvCardContent.text = viewData.question
                        binding.tvCardContent.typeface = Typeface.DEFAULT_BOLD
                    }

                    oa2.start()
                }
            })

            oa1.start()
            oa1.setDuration(150)
            oa2.setDuration(150)
        }
    }
}
fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
    val smoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int = snapMode
        override fun getHorizontalSnapPreference(): Int = snapMode
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)

}