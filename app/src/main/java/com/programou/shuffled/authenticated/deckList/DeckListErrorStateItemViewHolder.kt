package com.programou.shuffled.authenticated.deckList

import android.view.LayoutInflater
import android.view.ViewGroup
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.databinding.ViewDeckListErrorStateItemBinding

class DeckListErrorStateItemViewHolder private constructor(private val binding: ViewDeckListErrorStateItemBinding, private val onClick: Bind<DeckListFragment.AllDecksListState>?): ItemViewHolder<DeckListFragment.AllDecksListState>(binding.root) {
    companion object {
        val IDENTIFIER: Int by lazy { DeckListErrorStateItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup, onClick: Bind<DeckListFragment.AllDecksListState>? = null): DeckListErrorStateItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckListErrorStateItemBinding.inflate(inflater, parent, false)

            return DeckListErrorStateItemViewHolder(binding, onClick)
        }
    }

    override fun bind(viewData: DeckListFragment.AllDecksListState) {
        with(viewData.error!!) {
            binding.tvTitle.text = title
            binding.tvDescription.text = reason
        }
    }
}