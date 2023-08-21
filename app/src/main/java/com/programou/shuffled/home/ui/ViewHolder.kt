package com.programou.shuffled.home.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder<ViewData>(view: View): RecyclerView.ViewHolder(view) {
    abstract fun bind(viewData: ViewData)
}