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
    private val viewModel: DeckViewModel by lazy {
        val client = InmemoryDeckListClient.shared
        DeckViewModel(deckArgs.deckId, client, client)
    }

    private var isEditState = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDeckBinding.bind(view)

        onViewCreated()
    }

    private fun onViewCreated() {
        setupLayout()
        fetchDeckById()
    }

    private fun setupLayout() {
        setupListeners()
        updateStartButtonState()
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
            this.imageUri = deckViewData.image

            val requestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.color.gray_100)

            favoriteStateIndicatorHandler(deckViewData.isFavorite)

            Glide.with(binding.root.context)
                .load(deckViewData.image)
                .apply(requestOptions)
                .into(binding.deckImageViewInDeckFragment)

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
            if (isEditState) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                this.launcher.launch(galleryIntent)
            }
        }
        binding.backArrowIndicatorImageViewInDeckFragment.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.removeIndicatorImageViewInDeckFragment.setOnClickListener {
            val deckId = deckArgs.deckId
            viewModel.deleteDeck(deckId)
            findNavController().popBackStack()
        }

        binding.favoriteIndicatorImageViewInDeckFragment.setOnClickListener {
            //toggleFavoritedState()
            viewModel.toggleFavorite()
        }

        binding.editPencilIndicatorImageViewInDeckFragment.setOnClickListener {
            changeEditStateHandler()
        }

        binding.addNewCardButtonInDeckFragment.setOnClickListener {
            CreateEditCardBottomSheet(requireContext(), null, onDone = { card ->
                viewModel.createCard(deckArgs.deckId, Card(null, card.question, card.anwser))
                fetchDeckById()
                updateStartButtonState()
            }).show()
        }

        binding.studyButtonInDeckFragment.setOnClickListener {

            if (isEditState) {
                val cards = cardPreviewAdapter
                    .getViewData()
                    .filter { it.id != null }
                    .map { Card(it.id, it.question, it.anwser) }
                val title = binding.deckTitleEditTextInDeckFragment.text.toString()
                val description = binding.deckDescriptionEditTextInDeckFragment.text.toString()
                val isFavorited = viewModel.deckIsFavorite.value ?: false

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
    }

    private fun setupItemViewHoldersRegistration() {
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
                    }, onDelete = { cardViewData ->
                        val cards = cardPreviewAdapter.getViewData().toMutableList()
                        val index = cards.indexOfFirst { it.id == cardViewData.id }
                        cards.removeAt(index)

                        if (cards.isEmpty()) {
                            val itemViewData = ItemViewData(CardEmptyStateItemViewHolder.IDENTIFIER, PreviewViewData(null, "", ""))
                            cardPreviewAdapter.update(listOf(itemViewData))
                        } else {
                            cardPreviewAdapter.update(cards.map {
                                ItemViewData(CardPreviewItemViewHolder.IDENTIFIER, it)
                            })
                        }
                    }).show()
                }
            }
        }
    }

    private fun changeEditStateHandler() {
        isEditState = !isEditState

        binding.deckTitleEditTextInDeckFragment.isEnabled = isEditState
        binding.deckDescriptionEditTextInDeckFragment.isEnabled = isEditState

        if (isEditState) {
            binding.editPencilIndicatorImageViewInDeckFragment.setImageResource(R.drawable.ic_no_edit)
            binding.studyButtonInDeckFragment.text = "Salvar"
        } else {
            binding.editPencilIndicatorImageViewInDeckFragment.setImageResource(R.drawable.ic_edit)
            binding.studyButtonInDeckFragment.text = "Começar"
            fetchDeckById()
        }

        binding.backArrowIndicatorImageViewInDeckFragment.isVisible = !isEditState
        binding.favoriteIndicatorImageViewInDeckFragment.isVisible = !isEditState
        binding.removeIndicatorImageViewInDeckFragment.isVisible = isEditState

        updateStartButtonState()
    }

//    private fun toggleFavoritedState() {
//        viewModel.toggleFavorite()
//        isFavorited = isFavorited.not()
//
//        if (isFavorited) {
//            binding.favoriteIndicatorImageViewInDeckFragment.setColorFilter(requireContext().getColor(R.color.yellow_500))
//        } else {
//            binding.favoriteIndicatorImageViewInDeckFragment.setColorFilter(requireContext().getColor(R.color.gray_300))
//        }
//
//        viewModel.updateFavorite(deckArgs.deckId, isFavorited)
//    }

    private fun fetchDeckById() {
        viewModel.findDeckBy(deckArgs.deckId)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val galleryImageUri = it.data?.data
            imageUri = galleryImageUri
            binding.deckImageViewInDeckFragment.setImageURI(galleryImageUri)
        }
    }

    private fun updateStartButtonState() {
        val isNotEmpty = cardPreviewAdapter.getViewData().filter { it.id != null }.isNotEmpty()
        binding.studyButtonInDeckFragment.isEnabled = isNotEmpty || isEditState
        binding.studyButtonInDeckFragment.isActivated = isNotEmpty || isEditState
        binding.studyButtonInDeckFragment.isClickable = isNotEmpty || isEditState
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