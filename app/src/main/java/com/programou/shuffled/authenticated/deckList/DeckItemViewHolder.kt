package com.programou.shuffled.authenticated.deckList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.programou.shuffled.LoadImage
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.databinding.ViewDeckListItemBinding
import kotlinx.coroutines.async


class DeckItemViewHolder private constructor(
    private val activity: FragmentActivity,
    private val binding: ViewDeckListItemBinding,
    private val imageLoader: LoadImage,
    private val onClick: Bind<DeckListFragment.AllDecksListState>?
): ItemViewHolder<DeckItemViewHolder.ViewData>(binding.root) {
    companion object {
        val IDENTIFIER: Int by lazy { DeckItemViewHolder.hashCode() }

        fun instantiate(activity: FragmentActivity, parent: ViewGroup, imageLoader: LoadImage, onClick: Bind<DeckListFragment.AllDecksListState>?): DeckItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckListItemBinding.inflate(inflater, parent, false)

            return DeckItemViewHolder(activity, binding, imageLoader, onClick)
        }
    }

    override fun bind(viewData: DeckItemViewHolder.ViewData) {
        binding.textDeckTitle.text = viewData.title
        binding.totalOfCardsTextViewInDeckListItemView.text = viewData.numberOfFlashcards

        activity.lifecycleScope.async {
            val uri = viewData.imageUrl.toUri()
            imageLoader.loadFrom(uri, binding.imageDeck, binding.root.context)
        }

        binding.cardContainer.setOnClickListener {
            onClick?.let{  }
        }
    }

    data class ViewData(val title: String, val imageUrl: String, val numberOfFlashcards: String)
}
