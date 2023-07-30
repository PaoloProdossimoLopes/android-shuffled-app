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
    private val viewModel: DeckViewModel by lazy {
        val client = InmemoryDeckListClient.shared
        DeckViewModel(deckArgs.deckId, client, client)
    }
    private var imageUri: Uri? = null
        set(value) {
            field = value
            updateDeckImage(value)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDeckBinding.bind(view)

        onViewCreated()
    }

    private fun onViewCreated() {
        setupLayout()
        viewModel.loadDeck()
    }

    private fun setupLayout() {
        setupListeners()
        setupItemViewHoldersRegistration()
        setupRecyclerView()
        setupViewModelBinding()
    }

    private fun setupViewModelBinding() {
        viewModel.deckIsFavorite.observe(requireActivity()) { isFavorite ->
            favoriteStateIndicatorHandler(isFavorite)
        }

        viewModel.deckLiveData.observe(requireActivity()) { deckViewData ->
            binding.deckTitleEditTextInDeckFragment.setText(deckViewData.title)
            binding.deckDescriptionEditTextInDeckFragment.setText(deckViewData.description)
            imageUri = deckViewData.image

            favoriteStateIndicatorHandler(deckViewData.isFavorite)
        }

        viewModel.onCardListEmptyState.observe(requireActivity()) {
            val itemViewData = ItemViewData(CardEmptyStateItemViewHolder.IDENTIFIER, PreviewViewData(null, "", ""))
            cardPreviewAdapter.update(listOf(itemViewData))
            disableStudyButton()
        }

        viewModel.onCardListIsNotEmpty.observe(requireActivity()) { cardsViewData ->
            cardPreviewAdapter.update(cardsViewData.map {
                ItemViewData(
                    CardPreviewItemViewHolder.IDENTIFIER,
                    PreviewViewData(it.id, it.question, it.answer)
                )
            })
            enableStudyButton()
        }

        viewModel.onEnableEditMode.observe(requireActivity()) {
            shouldEnableDeckEditText(true)
            shouldEnableToolButtonsForEditMode(false)
            binding.editPencilIndicatorImageViewInDeckFragment.setImageResource(R.drawable.ic_no_edit)
            binding.studyButtonInDeckFragment.text = "Salvar"
            enableStudyButton()
        }

        viewModel.onDisableEditMode.observe(requireActivity()) {
            shouldEnableDeckEditText(false)
            shouldEnableToolButtonsForEditMode(true)
            binding.editPencilIndicatorImageViewInDeckFragment.setImageResource(R.drawable.ic_edit)
            binding.studyButtonInDeckFragment.text = "Começar"
            viewModel.loadDeck()
        }
    }
    private fun shouldEnableToolButtonsForEditMode(isEnable: Boolean) {
        binding.backArrowIndicatorImageViewInDeckFragment.isVisible = isEnable
        binding.favoriteIndicatorImageViewInDeckFragment.isVisible = isEnable
        binding.removeIndicatorImageViewInDeckFragment.isVisible = !isEnable
    }

    private fun shouldEnableDeckEditText(isEnable: Boolean) {
        binding.deckTitleEditTextInDeckFragment.isEnabled = false
        binding.deckDescriptionEditTextInDeckFragment.isEnabled = false
    }

    private fun favoriteStateIndicatorHandler(isFavorite: Boolean) {
        val color = requireContext().getColor(if (isFavorite) {
            R.color.yellow_500
        } else {
            R.color.gray_300
        })

        binding.favoriteIndicatorImageViewInDeckFragment.setColorFilter(color)
    }

    private fun setupRecyclerView() {
        binding.cardsRecyclerViewInDeckFragment.adapter = cardPreviewAdapter
        binding.cardsRecyclerViewInDeckFragment.layoutManager =
            GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        binding.cardsRecyclerViewInDeckFragment.setNestedScrollingEnabled(false);
    }

    private fun setupListeners() {
        binding.deckImageViewInDeckFragment.setOnClickListener {
            if (viewModel.isEditMode()) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                this.launcher.launch(galleryIntent)
            }
        }
        binding.backArrowIndicatorImageViewInDeckFragment.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.removeIndicatorImageViewInDeckFragment.setOnClickListener {
            viewModel.deleteDeck()
            findNavController().popBackStack()
        }

        binding.favoriteIndicatorImageViewInDeckFragment.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.editPencilIndicatorImageViewInDeckFragment.setOnClickListener {
            viewModel.changeEditMode()
        }

        binding.addNewCardButtonInDeckFragment.setOnClickListener {
            CreateEditCardBottomSheet(requireContext(), null, onDone = { cardViewData ->
                val card = Card(null, cardViewData.question, cardViewData.anwser)
                createCard(card)
            }).show()
        }

        binding.studyButtonInDeckFragment.setOnClickListener {
            if (viewModel.isEditMode()) {
                val cards = cardPreviewAdapter
                    .getViewData()
                    .filter { it.id != null }
                    .map { Card(it.id, it.question, it.anwser) }
                val title = binding.deckTitleEditTextInDeckFragment.text.toString()
                val description = binding.deckDescriptionEditTextInDeckFragment.text.toString()
                val isFavorited = viewModel.deckIsFavorite.value ?: false

                val deck = Deck(deckArgs.deckId, title, description, imageUri.toString(), isFavorited, cards)
                viewModel.updateDeck(deck)
                viewModel.loadDeck()
                viewModel.changeEditMode()
            } else {
                viewModel.deckLiveData.value?.let { deck ->
                    val deck = Deck(deckArgs.deckId, deck.title, deck.description, deck.image.toString(), deck.isFavorite, deck.cards.map { card -> Card(card.id, card.question, card.answer) })
                    val action = DeckFragmentDirections.actionDeckFragmentToFlashCardFragment(deck)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun createCard(card: Card) {
        viewModel.createCard(card)
        enableStudyButton()
    }

    private fun setupItemViewHoldersRegistration() {
        cardPreviewAdapter.register(CardEmptyStateItemViewHolder.IDENTIFIER) { parent ->
            CardEmptyStateItemViewHolder.instantiate(parent)
        }

        cardPreviewAdapter.register(CardPreviewItemViewHolder.IDENTIFIER) { parent ->
            CardPreviewItemViewHolder.instantiate(parent) { previewViewData ->
                if (viewModel.isEditMode() == false) return
                presentCreateEditBottomSheet(previewViewData)
            }
        }
    }

    private fun presentCreateEditBottomSheet(cardPreviewViewData: PreviewViewData) {
        val bottomSheet = CreateEditCardBottomSheet(requireContext(), cardPreviewViewData, onDone = {
            updateCard(cardPreviewViewData)
        }, onDelete = { cardViewData ->
            removeCard(cardViewData)
        })
        bottomSheet.show()
    }

    private fun updateCard(viewData: PreviewViewData) {
        val card = Card(viewData.id, viewData.question, viewData.anwser)
        viewModel.updateCard(card)
    }

    private fun removeCard(viewData: PreviewViewData) {
        val cardId = viewData.id!!
        viewModel.removeCard(cardId)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            imageUri = it.data?.data
        }
    }

    private fun updateDeckImage(uri: Uri?) {
        val requestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.color.gray_100)
        Glide.with(binding.root.context)
            .load(uri)
            .apply(requestOptions)
            .into(binding.deckImageViewInDeckFragment)
    }

    private fun enableStudyButton() {
        binding.studyButtonInDeckFragment.isEnabled = true
        binding.studyButtonInDeckFragment.isActivated = true
        binding.studyButtonInDeckFragment.isClickable = true
    }

    private fun disableStudyButton() {
        binding.studyButtonInDeckFragment.isEnabled = false
        binding.studyButtonInDeckFragment.isActivated = false
        binding.studyButtonInDeckFragment.isClickable = false
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