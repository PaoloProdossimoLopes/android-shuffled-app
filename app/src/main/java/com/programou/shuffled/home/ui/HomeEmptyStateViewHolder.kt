package com.programou.shuffled.home.ui

import com.programou.shuffled.databinding.ViewDeckListEmptyStateItemBinding

class HomeEmptyStateViewHolder(private val binding: ViewDeckListEmptyStateItemBinding): ViewHolder<HomeEmptyStateViewHolder.ViewData>(binding.root) {

    companion object {
        val identifier = HomeEmptyStateViewHolder::class.java.hashCode()
    }

    override fun bind(viewData: ViewData) {
        binding.tvTitle.text = viewData.title
        binding.tvDescription.text = viewData.description
    }

    data class ViewData(val title: String, val description: String)
}