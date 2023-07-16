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
import com.programou.shuffled.databinding.FragmentFlashCardBinding

class FlashCardFragment : Fragment(R.layout.fragment_flash_card) {
    private lateinit var binding: FragmentFlashCardBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFlashCardBinding.bind(view)

        binding.imageBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

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