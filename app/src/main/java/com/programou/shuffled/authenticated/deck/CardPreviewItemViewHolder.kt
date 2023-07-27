package com.programou.shuffled.authenticated.deck

import android.view.LayoutInflater
import android.view.ViewGroup
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.authenticated.deckList.Bind
import com.programou.shuffled.databinding.ViewDeckCardPreviewItemBinding


class CardPreviewItemViewHolder private constructor(private val binding: ViewDeckCardPreviewItemBinding, private val onClick: Bind<PreviewViewData>): ItemViewHolder<PreviewViewData>(binding.root) {

    companion object {
        val IDENTIFIER: Int by lazy { CardPreviewItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup, onClick: Bind<PreviewViewData>): CardPreviewItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckCardPreviewItemBinding.inflate(inflater, parent, false)

            return CardPreviewItemViewHolder(binding, onClick)
        }
    }

    override fun bind(viewData: PreviewViewData) {
        binding.tvQuestion.setText(viewData.question)
        binding.tvAnswer.setText(viewData.anwser)

        binding.root.setOnClickListener {
            onClick(viewData)
        }
    }
}