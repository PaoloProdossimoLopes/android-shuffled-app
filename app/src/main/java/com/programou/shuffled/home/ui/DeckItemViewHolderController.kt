package com.programou.shuffled.home.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programou.shuffled.LoadImage
import com.programou.shuffled.databinding.ViewDeckListItemBinding

class DeckItemViewHolderController(
    private val viewData: DeckItemViewHolder.ViewData
): ViewHolderController {

    override fun getType() = DeckItemViewHolder.Identifier

    override fun bindForViewHolder(viewHolder: RecyclerView.ViewHolder) {
        (viewHolder as DeckItemViewHolder).bind(viewData)
    }
}