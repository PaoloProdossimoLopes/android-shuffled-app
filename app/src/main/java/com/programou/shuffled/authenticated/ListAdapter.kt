package com.programou.shuffled.authenticated

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class ListAdapter<D>: RecyclerView.Adapter<ItemViewHolder<D>>() {

    private var itens = listOf<ItemViewData<D>>()
    private var itensRegisters = mutableMapOf<Int, (parent: ViewGroup) -> ItemViewHolder<D>>()

    fun register(itemIdentifier: Int, factory: (parent: ViewGroup) -> ItemViewHolder<D>) {
        itensRegisters[itemIdentifier] = factory
    }

    fun update(newItens: List<ItemViewData<D>>) {
        itens = newItens
        notifyDataSetChanged()
    }

    fun getViewData() = itens.map { it.viewData }

    override fun getItemCount() = itens.count()

    override fun getItemViewType(position: Int) = itens[position].identifier

    override fun onCreateViewHolder(parent: ViewGroup, type: Int) = itensRegisters[type]
        ?.invoke(parent) ?: throw ItemTypeNotFoundException()

    override fun onBindViewHolder(holder: ItemViewHolder<D>, position: Int) {
        val item = itens[position]
        holder.bind(item.viewData)
    }

    class ItemTypeNotFoundException: Error()
}