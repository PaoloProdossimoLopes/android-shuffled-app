package com.programou.shuffled.home.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programou.shuffled.databinding.ViewDeckListErrorStateItemBinding

class HomeErrorStateViewHolderController(private val viewData: HomeErrorStateViewHolder.ViewData):
    ViewHolderController {
    override fun getType(): Int = HomeErrorStateViewHolder::class.java.hashCode()

    override fun bindForViewHolder(viewHolder: RecyclerView.ViewHolder) {
        (viewHolder as HomeErrorStateViewHolder).bind(viewData)
    }
}