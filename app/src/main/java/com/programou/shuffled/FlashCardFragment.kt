package com.programou.shuffled

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.programou.shuffled.authenticated.ItemViewData
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.authenticated.ListAdapter
import com.programou.shuffled.authenticated.deck.PreviewViewData
import com.programou.shuffled.authenticated.deckList.Bind
import com.programou.shuffled.databinding.FragmentFlashCardBinding
import com.programou.shuffled.databinding.ViewDeckCardPreviewItemBinding
import com.programou.shuffled.databinding.ViewFlashCardItemBinding

class FlashCardFragment : Fragment(R.layout.fragment_flash_card) {
    private lateinit var binding: FragmentFlashCardBinding
    private val flashcardListAdapter = ListAdapter<FlashCardViewData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFlashCardBinding.bind(view)

        flashcardListAdapter.register(FlashCardItemViewHolder.IDENTIFIER) { parent ->
            FlashCardItemViewHolder.instantiate(parent)
        }

        binding.recyclerFlahscards.adapter = flashcardListAdapter
        binding.recyclerFlahscards.layoutManager = object: LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) {
            override fun canScrollHorizontally() = false
            override fun canScrollVertically() = false
        }

        binding.mcvGood.setOnClickListener {
            binding.recyclerFlahscards.post(object: Runnable {
                override fun run() {
                    binding.recyclerFlahscards.smoothScrollToPosition(flashcardListAdapter.itemCount - 1)
                }
            })
        }

        binding.mcvBad.setOnClickListener {
            binding.recyclerFlahscards.post(object: Runnable {
                override fun run() {
                    binding.recyclerFlahscards.smoothScrollToPosition(flashcardListAdapter.itemCount - 1)
                }
            })
        }

        binding.imageBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        flashcardListAdapter.update(listOf(
            ItemViewData(FlashCardItemViewHolder.IDENTIFIER, FlashCardViewData()),
            ItemViewData(FlashCardItemViewHolder.IDENTIFIER, FlashCardViewData()),
            ItemViewData(FlashCardItemViewHolder.IDENTIFIER, FlashCardViewData()),
            ItemViewData(FlashCardItemViewHolder.IDENTIFIER, FlashCardViewData()),
            ItemViewData(FlashCardItemViewHolder.IDENTIFIER, FlashCardViewData())
        ))
    }
}

class FlashCardViewData

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
        binding.imgFlipIndicator.setOnClickListener {
            val oa1 = ObjectAnimator.ofFloat(binding.cardContainer, "scaleX", 1f, 0f)
            val oa2 = ObjectAnimator.ofFloat(binding.cardContainer, "scaleX", 0f, 1f)

            oa1.interpolator = DecelerateInterpolator()
            oa2.interpolator = AccelerateInterpolator()

            oa1.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    if (binding.tvCardContent.text == "Pergunta") {
                        binding.tvCardContent.text = "Resposta"
                    } else {
                        binding.tvCardContent.text = "Pergunta"
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
