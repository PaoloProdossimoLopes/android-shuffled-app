package com.programou.shuffled.authenticated.deck

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewData
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.authenticated.ListAdapter
import com.programou.shuffled.authenticated.deckList.Bind
import com.programou.shuffled.databinding.FragmentDeckBinding
import com.programou.shuffled.databinding.ViewAddCardBottomSheetDialogBinding
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

        binding.textAddNewCard.setOnClickListener {
            val dialog = CreateEditCardBottomSheet(requireContext(), null)
            dialog.show()
        }

        cardPreviewAdapter.register(CardPreviewItemViewHolder.IDENTIFIER) { parent ->
            CardPreviewItemViewHolder.instantiate(parent) { previewViewData ->
                val viewData = CardViewData(previewViewData.question, previewViewData.anwser)
                val dialog = CreateEditCardBottomSheet(requireContext(), viewData)
                dialog.show()
            }
        }

        binding.recyclerCardsCarrousel.adapter = cardPreviewAdapter
        binding.recyclerCardsCarrousel.layoutManager = GridLayoutManager(requireContext(), 2 ,GridLayoutManager.HORIZONTAL, false)
        binding.recyclerCardsCarrousel.setNestedScrollingEnabled(false);

        binding.buttonStart.setOnClickListener {
            val action = DeckFragmentDirections.actionDeckFragmentToFlashCardFragment()
            findNavController().navigate(action)
        }

        cardPreviewAdapter.update(listOf(
            ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, PreviewViewData("Hello", "Olá")),
            ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, PreviewViewData("Whats up?", "e ai?")),
            ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, PreviewViewData("How's going?", "Como voce esta?")),
            ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, PreviewViewData("What you prefere", "Oque voce prefere")),
            ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, PreviewViewData("What did you mean?", "oque isso significa?"))
        ))
    }
}

data class PreviewViewData(val question: String, val anwser: String)

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
        binding.tvQuestion.setText(viewData.question)
        binding.tvAnswer.setText(viewData.anwser)

        binding.root.setOnClickListener {
            onClick(viewData)
        }
    }
}

class CreateEditCardBottomSheet(context: Context, private val cardViewData: CardViewData?): BottomSheetDialog(context) {

    private lateinit var binding: ViewAddCardBottomSheetDialogBinding
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ViewAddCardBottomSheetDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idBtnDismiss.setOnClickListener {
            dismiss()
        }

        binding.imageClose.setOnClickListener {
            dismiss()
        }

        binding.idBtnDismiss.setBackgroundColor(context.getColor(R.color.turquoise_500))
        binding.idBtnDismiss.setTextColor(context.getColor(R.color.white))

        cardViewData?.let { viewData ->
            binding.editTextCardQuestion.setText(viewData.question)
            binding.editTextCardAnswer.setText(viewData.anwser)
        }

        setCancelable(false)
    }
}

data class CardViewData(val question: String, val anwser: String)
