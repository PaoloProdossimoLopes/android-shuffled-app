package com.programou.shuffled.authenticated.deck

import android.view.LayoutInflater
import android.view.ViewGroup
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.databinding.ViewEmptyCardStateItemBinding


class CardEmptyStateItemViewHolder(binding: ViewEmptyCardStateItemBinding): ItemViewHolder<PreviewViewData>(binding.root) {

    companion object {
        val IDENTIFIER: Int by lazy { CardEmptyStateItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup): CardEmptyStateItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewEmptyCardStateItemBinding.inflate(inflater, parent, false)
            return CardEmptyStateItemViewHolder(binding)
        }
    }
    override fun bind(viewData: PreviewViewData) { }
}