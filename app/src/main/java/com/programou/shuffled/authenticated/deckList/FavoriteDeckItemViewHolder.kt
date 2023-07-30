package com.programou.shuffled.authenticated.deckList

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.databinding.ViewFavoriteDeckListItemBinding

class FavoriteDeckItemViewHolder private constructor(private val binding: ViewFavoriteDeckListItemBinding, private val onClick: Bind<DeckListFragment.FavoriteDecksListState>?): ItemViewHolder<DeckListFragment.FavoriteDecksListState>(binding.root) {
    companion object {
        val IDENTIFIER: Int by lazy { DeckItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup, onClick: Bind<DeckListFragment.FavoriteDecksListState>?): FavoriteDeckItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewFavoriteDeckListItemBinding.inflate(inflater, parent, false)

            return FavoriteDeckItemViewHolder(binding, onClick)
        }
    }

    override fun bind(viewData: DeckListFragment.FavoriteDecksListState) {
        with(viewData.deck!!) {
            binding.textDeckTitle.text = name
            binding.totalOfCardsTextViewInFavoritedDeckListItemView.text = numberOfCards

            val requestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.color.gray_100)

            Glide.with(binding.root.context)
                .load(thumbnailUrl)
                .apply(requestOptions)
                .into(binding.imageDeck)
        }

        binding.cardContainer.setOnClickListener {
            onClick?.let{ it(viewData) }
        }
    }
}