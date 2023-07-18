package com.programou.shuffled.authenticated.deckList

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.databinding.ViewFavoriteDeckListItemBinding

class FavoriteDeckItemViewHolder private constructor(private val binding: ViewFavoriteDeckListItemBinding, private val onClick: Bind<DeckListFavoriteItemViewData>?): ItemViewHolder<DeckListFavoriteItemViewData>(binding.root) {
    companion object {
        val IDENTIFIER: Int by lazy { DeckItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup, onClick: Bind<DeckListFavoriteItemViewData>?): FavoriteDeckItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewFavoriteDeckListItemBinding.inflate(inflater, parent, false)

            return FavoriteDeckItemViewHolder(binding, onClick)
        }
    }

    override fun bind(viewData: DeckListFavoriteItemViewData) {
        with(viewData.deck!!) {
            binding.textDeckTitle.text = name
            binding.tvTotalCards.text = numberOfCards

            val requestOptions = RequestOptions()
                .centerCrop()
                //.error(R.drawable.ic_no_image)
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