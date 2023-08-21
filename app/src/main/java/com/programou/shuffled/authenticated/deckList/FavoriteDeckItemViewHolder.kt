package com.programou.shuffled.authenticated.deckList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.programou.shuffled.LoadImage
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.databinding.ViewFavoriteDeckListItemBinding
import kotlinx.coroutines.async

class FavoriteDeckItemViewHolder private constructor(
    private val activity: FragmentActivity,
    private val binding: ViewFavoriteDeckListItemBinding,
    private val imageLoader: LoadImage,
    private val onClick: Bind<DeckListFragment.FavoriteDecksListState>?
): ItemViewHolder<DeckListFragment.FavoriteDecksListState>(binding.root) {
    companion object {
        val IDENTIFIER: Int by lazy { DeckItemViewHolder.hashCode() }

        fun instantiate(activity: FragmentActivity, parent: ViewGroup, imageLoader: LoadImage, onClick: Bind<DeckListFragment.FavoriteDecksListState>?): FavoriteDeckItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewFavoriteDeckListItemBinding.inflate(inflater, parent, false)

            return FavoriteDeckItemViewHolder(activity, binding, imageLoader, onClick)
        }
    }

    override fun bind(viewData: DeckListFragment.FavoriteDecksListState) {
        with(viewData.deck!!) {
            binding.textDeckTitle.text = name
            binding.totalOfCardsTextViewInFavoritedDeckListItemView.text = numberOfCards


            activity.lifecycleScope.async {
                imageLoader.loadFrom(thumbnailUrl.toUri(), binding.imageDeck, activity)
            }
        }

        binding.cardContainer.setOnClickListener {
            onClick?.let{ it(viewData) }
        }
    }
}