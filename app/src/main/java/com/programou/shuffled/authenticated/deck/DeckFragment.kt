package com.programou.shuffled.authenticated.deck

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.programou.shuffled.InmemoryDeckListClient
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewData
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.authenticated.ListAdapter
import com.programou.shuffled.authenticated.deckList.Card
import com.programou.shuffled.authenticated.deckList.Deck
import com.programou.shuffled.databinding.FragmentDeckBinding
import com.programou.shuffled.databinding.ViewEmptyCardStateItemBinding

class DeckFragment : Fragment(R.layout.fragment_deck) {
    private lateinit var binding: FragmentDeckBinding

    private val cardPreviewAdapter = ListAdapter<PreviewViewData>()
    private val deckArgs: DeckFragmentArgs by navArgs()
    private var imageUri: Uri? = null
    private var isFavorited = true
    private val viewModel: DeckViewModel by lazy {
        val client = InmemoryDeckListClient.shared
        val findRepository = DeckRepository(client)
        val updateRepository = DeckUpdateRepository(client)
        val findUseCase = DeckFinderUseCase(findRepository)
        val updateUseCase = DeckUpdate(updateRepository)
        DeckViewModel(findUseCase, updateUseCase)
    }

    private var isEditState = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDeckBinding.bind(view)

        binding.imageBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.removeIndicatorImageView.setOnClickListener {
            val deckId = deckArgs.deckId
            viewModel.deleteDeck(deckId)
            findNavController().popBackStack()
        }

        binding.favoriteIndicatorImageView.setOnClickListener {
            isFavorited = !isFavorited

            if (isFavorited) {
                binding.favoriteIndicatorImageView.setColorFilter(requireContext().getColor(R.color.yellow_500))
            } else {
                binding.favoriteIndicatorImageView.setColorFilter(requireContext().getColor(R.color.gray_300))
            }

            viewModel.updateFavorite(deckArgs.deckId, isFavorited)
        }

        binding.editDeckTitle.setText("Ingles")

        updateStartButtonState()

        binding.imageEditFields.setOnClickListener {
            changeEditStateHandler()
        }

        binding.textAddNewCard.setOnClickListener {
            CreateEditCardBottomSheet(requireContext(), null, onDone = { card ->
                viewModel.createCard(deckArgs.deckId, Card(null, card.question, card.anwser))
                viewModel.findDeckBy(deckArgs.deckId)
                updateStartButtonState()
            }).show()
        }

        cardPreviewAdapter.register(CardEmptyStateItemViewHolder.IDENTIFIER) { parent ->
            CardEmptyStateItemViewHolder.instantiate(parent)
        }

        cardPreviewAdapter.register(CardPreviewItemViewHolder.IDENTIFIER) { parent ->
            CardPreviewItemViewHolder.instantiate(parent) { previewViewData ->
                if (isEditState) {
                    CreateEditCardBottomSheet(requireContext(), previewViewData, onDone = { card ->
                        val cards = cardPreviewAdapter.getViewData()
                        val index = cards.indexOfFirst { it.id == card.id }
                        cards[index].anwser = card.anwser
                        cards[index].question = card.question
                        cardPreviewAdapter.update(cards.map {
                            ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, it)
                        })
                    }).show()
                }
            }
        }

        binding.recyclerCardsCarrousel.adapter = cardPreviewAdapter
        binding.recyclerCardsCarrousel.layoutManager =
            GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerCardsCarrousel.setNestedScrollingEnabled(false);

        binding.buttonStart.setOnClickListener {

            if (isEditState) {
                val cards = cardPreviewAdapter.getViewData().map { Card(it.id, it.question, it.anwser) }
                val title = binding.editDeckTitle.text.toString()
                val description = binding.editDeckDescription.text.toString()

                val deck = Deck(deckArgs.deckId, title, description, imageUri.toString(), isFavorited, cards)
                changeEditStateHandler()
                viewModel.updateDeck(deck)
                viewModel.findDeckBy(deck.id)
                updateStartButtonState()
            } else {
                viewModel.deckLiveData.value?.let { deck ->
                    val deck = Deck(deckArgs.deckId, deck.title, deck.description, deck.image.toString(), deck.isFavorite, deck.cards.map { card -> Card(card.id, card.question, card.answer) })
                    val action = DeckFragmentDirections.actionDeckFragmentToFlashCardFragment(deck)
                    findNavController().navigate(action)
                }
            }
        }

        binding.deckImageView.setOnClickListener {
            if (isEditState) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                this.launcher.launch(galleryIntent)
            }
        }

        viewModel.deckLiveData.observe(requireActivity()) { deckViewData ->
            binding.editDeckTitle.setText(deckViewData.title)
            binding.editDeckDescription.setText(deckViewData.description)
            this.imageUri = deckViewData.image
            this.isFavorited = deckViewData.isFavorite

            val requestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.color.gray_100)

            if (isFavorited) {
                binding.favoriteIndicatorImageView.setColorFilter(requireContext().getColor(R.color.yellow_500))
            } else {
                binding.favoriteIndicatorImageView.setColorFilter(requireContext().getColor(R.color.gray_300))
            }

            Glide.with(binding.root.context)
                .load(deckViewData.image)
                .apply(requestOptions)
                .into(binding.deckImageView)

            if (deckViewData.cards.isEmpty()) {
                val itemViewData = ItemViewData(CardEmptyStateItemViewHolder.IDENTIFIER, PreviewViewData(null, "", ""))
                 cardPreviewAdapter.update(listOf(itemViewData))
            } else {
                cardPreviewAdapter.update(deckViewData.cards.map {
                    ItemViewData(
                        CardPreviewItemViewHolder.IDENTIFIER,
                        PreviewViewData(it.id, it.question, it.answer)
                    )
                })
            }

            updateStartButtonState()
        }

        viewModel.findDeckBy(deckArgs.deckId)
    }

    private fun changeEditStateHandler() {
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

        binding.imageBackArrow.isVisible = !isEditState
        binding.favoriteIndicatorImageView.isVisible = !isEditState
        binding.removeIndicatorImageView.isVisible = isEditState
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val galleryImageUri = it.data?.data
            imageUri = galleryImageUri
            binding.deckImageView.setImageURI(galleryImageUri)
        }
    }

    private fun updateStartButtonState() {
        val isNotEmpty = !cardPreviewAdapter.getViewData().isEmpty()
        binding.buttonStart.isEnabled = isNotEmpty
        binding.buttonStart.isActivated = isNotEmpty
        binding.buttonStart.isClickable = isNotEmpty
    }
}

class CardEmptyStateItemViewHolder(binding: ViewEmptyCardStateItemBinding): ItemViewHolder<PreviewViewData>(binding.root) {

    companion object {
        val IDENTIFIER: Int by lazy { CardEmptyStateItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup): CardEmptyStateItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewEmptyCardStateItemBinding.inflate(inflater, parent, false)
            return CardEmptyStateItemViewHolder(binding)
        }
    }
    override fun bind(viewData: PreviewViewData) { }
}