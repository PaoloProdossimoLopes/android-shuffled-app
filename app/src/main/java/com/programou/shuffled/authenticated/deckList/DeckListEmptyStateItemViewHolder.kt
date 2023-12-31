package com.programou.shuffled.authenticated.deckList

import android.view.LayoutInflater
import android.view.ViewGroup
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.databinding.ViewDeckListEmptyStateItemBinding


class DeckListEmptyStateItemViewHolder private constructor(private val binding: ViewDeckListEmptyStateItemBinding, private val onClick: Bind<DeckListFragment.AllDecksListState>?): ItemViewHolder<DeckListFragment.AllDecksListState>(binding.root) {
    companion object {
        val IDENTIFIER: Int by lazy { DeckListEmptyStateItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup, onClick: Bind<DeckListFragment.AllDecksListState>? = null): DeckListEmptyStateItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckListEmptyStateItemBinding.inflate(inflater, parent, false)

            return DeckListEmptyStateItemViewHolder(binding, onClick)
        }
    }

    override fun bind(viewData: DeckListFragment.AllDecksListState) {
        with(viewData.empty!!) {
            binding.tvTitle.text = title
            binding.tvDescription.text = message
        }
    }
}

