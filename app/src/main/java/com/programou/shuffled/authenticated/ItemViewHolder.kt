package com.programou.shuffled.authenticated

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ItemViewHolder<D>(view: View): RecyclerView.ViewHolder(view) {
    abstract fun bind(viewData: D)
}