package com.programou.shuffled.home.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface ViewHolderController{
    fun getType(): Int
    fun bindForViewHolder(viewHolder: RecyclerView.ViewHolder)
}