package com.programou.shuffled.authenticated.flashcard

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.databinding.ViewFlashCardItemBinding


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