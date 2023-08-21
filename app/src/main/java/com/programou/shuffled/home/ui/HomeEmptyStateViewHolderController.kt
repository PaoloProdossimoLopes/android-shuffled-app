package com.programou.shuffled.home.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programou.shuffled.databinding.ViewDeckListEmptyStateItemBinding

class HomeEmptyStateViewHolderController(private val viewData: HomeEmptyStateViewHolder.ViewData):
    ViewHolderController {

    override fun getType() = HomeEmptyStateViewHolder::class.java.hashCode()

    override fun bindForViewHolder(viewHolder: RecyclerView.ViewHolder) {
        (viewHolder as HomeEmptyStateViewHolder).bind(viewData)
    }
}