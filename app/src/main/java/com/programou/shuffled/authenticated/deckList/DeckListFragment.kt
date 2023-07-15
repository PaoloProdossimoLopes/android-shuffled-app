package com.programou.shuffled.authenticated.deckList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewData
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.authenticated.ListAdapter
import com.programou.shuffled.databinding.FragmentDeckListBinding
import com.programou.shuffled.databinding.ViewDeckListItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeckListFragment : Fragment(R.layout.fragment_deck_list) {

    private lateinit var binding: FragmentDeckListBinding
    private val deckListAdapter = ListAdapter<DeckViewData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDeckListBinding.bind(view)

        deckListAdapter.register(DeckItemViewHolder.IDENTIFIER) { parent ->
            DeckItemViewHolder.instantiate(parent) { deckViewDataClicked ->
                val action = DeckListFragmentDirections.actionDecksFragmentToDeckFragment()
                findNavController().navigate(action)
            }
        }

        binding.progressDeckListLoader.visibility = View.VISIBLE

        binding.recyclerDecks.visibility = View.GONE
        binding.recyclerDecks.adapter = deckListAdapter
        binding.recyclerDecks.layoutManager = GridLayoutManager(requireContext(), 2)

        lifecycleScope.launch {
            updateMock()
        }
    }

    private suspend fun updateMock() {
        withContext(Dispatchers.IO) {
            delay(3000)

            val elements = listOf(
                ItemViewData(DeckItemViewHolder.IDENTIFIER, DeckViewData("Ingles")),
                ItemViewData(DeckItemViewHolder.IDENTIFIER, DeckViewData("Frances")),
                ItemViewData(DeckItemViewHolder.IDENTIFIER, DeckViewData("Computer Science")),
                ItemViewData(DeckItemViewHolder.IDENTIFIER, DeckViewData("Ingles")),
                ItemViewData(DeckItemViewHolder.IDENTIFIER, DeckViewData("Frances")),
                ItemViewData(DeckItemViewHolder.IDENTIFIER, DeckViewData("Computer Science"))
            )

            withContext(Dispatchers.Main) {
                deckListAdapter.update(elements)
                binding.progressDeckListLoader.visibility = View.GONE
                binding.recyclerDecks.visibility = View.VISIBLE
            }
        }
    }
}

data class DeckViewData(
    val title: String
)

typealias Bind<T> = (T) -> Unit

class DeckItemViewHolder private constructor(private val binding: ViewDeckListItemBinding, private val onClick: Bind<DeckViewData>?): ItemViewHolder<DeckViewData>(binding.root) {
    companion object {
        val IDENTIFIER: Int by lazy { DeckItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup, onClick: Bind<DeckViewData>?): DeckItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckListItemBinding.inflate(inflater, parent, false)

            return DeckItemViewHolder(binding, onClick)
        }
    }

    override fun bind(viewData: DeckViewData) {
        binding.editDeckTitle.text = viewData.title
        binding.cardContainer.setOnClickListener {
            onClick?.let{ it(viewData) }
        }
    }
}
