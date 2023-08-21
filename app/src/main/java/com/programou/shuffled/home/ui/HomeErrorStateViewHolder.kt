package com.programou.shuffled.home.ui

import android.graphics.BlendMode
import android.graphics.PorterDuff
import com.programou.shuffled.R
import com.programou.shuffled.databinding.ViewDeckListErrorStateItemBinding

class HomeErrorStateViewHolder(
    private val binding: ViewDeckListErrorStateItemBinding,
    private val onClick: () -> Unit
): ViewHolder<HomeErrorStateViewHolder.ViewData>(binding.root) {
    companion object {
        val identifier = HomeErrorStateViewHolder::class.java.hashCode()
    }

    override fun bind(viewData: ViewData) {
        binding.tvTitle.text = viewData.title
        binding.tvDescription.text = viewData.reason
        binding.homeErrorStateRetryButtonTextView.text = binding.root.context.getString(
            R.string.homeErrorStateRetryButtonTitleText
        )
        binding.homeErrorStateRetryButtonTextView.setOnClickListener { onClick() }
    }

    data class ViewData(val title: String, val reason: String)
}