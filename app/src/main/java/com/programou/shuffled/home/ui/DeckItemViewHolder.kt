package com.programou.shuffled.home.ui

import androidx.core.net.toUri
import com.programou.shuffled.LoadImage
import com.programou.shuffled.databinding.ViewDeckListItemBinding

class DeckItemViewHolder(
    private val binding: ViewDeckListItemBinding,
    private val imageLoader: LoadImage,
    private val onClick: (ViewData) -> Unit
): ViewHolder<DeckItemViewHolder.ViewData>(binding.root) {
    companion object {
        val Identifier = DeckItemViewHolder::class.java.hashCode()
    }

    override fun bind(viewData: ViewData) {
        binding.textDeckTitle.text = viewData.title
        binding.totalOfCardsTextViewInDeckListItemView.text = viewData.numberOfFlashcards
        imageLoader.loadFrom(viewData.imageUrl.toUri(), binding.imageDeck, binding.root.context)
        binding.root.setOnClickListener {
            onClick(viewData)
        }
    }

    data class ViewData(val id: Long, val title: String, val imageUrl: String, val numberOfFlashcards: String)
}