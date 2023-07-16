package com.programou.shuffled.authenticated.deck

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewData
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.authenticated.ListAdapter
import com.programou.shuffled.authenticated.deckList.Bind
import com.programou.shuffled.databinding.FragmentDeckBinding
import com.programou.shuffled.databinding.ViewDeckCardPreviewItemBinding

class DeckFragment : Fragment(R.layout.fragment_deck) {
    private lateinit var binding: FragmentDeckBinding

    private val cardPreviewAdapter = ListAdapter<PreviewViewData>()

    private var isEditState = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDeckBinding.bind(view)

        binding.imageBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editDeckTitle.setText("Ingles")

        binding.imageEditFields.setOnClickListener {
            isEditState = !isEditState

            binding.editDeckTitle.isEnabled = isEditState
            binding.editDeckDescription.isEnabled = isEditState

            if (isEditState) {
                binding.imageEditFields.setImageResource(R.drawable.ic_no_edit)
                binding.buttonStart.text = "Salvar"
            } else {
                binding.imageEditFields.setImageResource(R.drawable.ic_edit)
                binding.buttonStart.text = "Começar"
            }
        }

        cardPreviewAdapter.register(CardPreviewItemViewHolder.IDENTIFIER) { parent ->
            CardPreviewItemViewHolder.instantiate(parent) { clickedCardPreview ->

            }
        }

        binding.recyclerCardsCarrousel.adapter = cardPreviewAdapter
        binding.recyclerCardsCarrousel.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.buttonStart.setOnClickListener {
            val action = DeckFragmentDirections.actionDeckFragmentToFlashCardFragment()
            findNavController().navigate(action)
        }

        cardPreviewAdapter.update(listOf(
            ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, PreviewViewData()),
            ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, PreviewViewData()),
            ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, PreviewViewData()),
            ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, PreviewViewData()),
            ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, PreviewViewData()),
        ))
    }
}

class PreviewViewData

class CardPreviewItemViewHolder private constructor(private val binding: ViewDeckCardPreviewItemBinding, private val onClick: Bind<PreviewViewData>): ItemViewHolder<PreviewViewData>(binding.root) {

    companion object {
        val IDENTIFIER: Int by lazy { CardPreviewItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup, onClick: Bind<PreviewViewData>): CardPreviewItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckCardPreviewItemBinding.inflate(inflater, parent, false)

            return CardPreviewItemViewHolder(binding, onClick)
        }
    }

    override fun bind(viewData: PreviewViewData) {
        binding.root.setOnClickListener {
            onClick(viewData)
        }
    }
}
