package com.programou.shuffled.home.ui

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalArgumentException

class HomeDeckRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data: MutableList<ViewHolderController>
    private val registers: MutableMap<Int, (ViewGroup) -> RecyclerView.ViewHolder> = mutableMapOf()
    private var currentPosition = 0

    init {
        data = ArrayList()
    }

    fun update(newDatas: List<ViewHolderController>) {
        data.clear()
        data.addAll(newDatas)
        notifyDataSetChanged()
    }

    fun register(typeId: Int, map: (ViewGroup) -> RecyclerView.ViewHolder) {
        registers[typeId] = map
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val vh = registers[viewType]?.invoke(parent)
        if (vh == null) throw IllegalArgumentException("The view holder type not fund: ${viewType}")
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        data[position].bindForViewHolder(holder)
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {
        currentPosition = position
        return data[position].getType()
    }
}