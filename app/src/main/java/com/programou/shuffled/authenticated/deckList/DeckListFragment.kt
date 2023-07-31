package com.programou.shuffled.authenticated.deckList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.programou.shuffled.FirebaseAuthClientProviderAdapter
import com.programou.shuffled.InmemoryDeckListClient
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewData
import com.programou.shuffled.authenticated.ListAdapter
import com.programou.shuffled.authenticated.createDeck.CreateDeckBottomSheetView
import com.programou.shuffled.databinding.FragmentDeckListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeckListFragment : Fragment(R.layout.fragment_deck_list) {

    class AllDecksListState (
        var deck: DeckListViewData.Deck? = null,
        var empty: DeckListViewData.Empty? = null,
        var error: DeckListViewData.Error? = null
    )

    class FavoriteDecksListState (
        var deck: DeckListViewData.Deck? = null,
    )

    private lateinit var binding: FragmentDeckListBinding

    private var createDeckDialog: CreateDeckBottomSheetView? = null

    private val deckListAdapter = ListAdapter<AllDecksListState>()
    private val recentDeckListAdapter = ListAdapter<FavoriteDecksListState>()

    private val favoriteDecksListViewModel: FavoriteDecksListViewModel by lazy {
        val repository = RemoteListFavoritedDecksRepository(InmemoryDeckListClient.shared)
        val useCase = ListFavoriteDecksUseCase(repository)
        FavoriteDecksListViewModel(useCase)
    }

    private val allDecksViewModel: DeckListViewModel by lazy {
        val listAllDecksRepository = RemoteListAllDeckRepository(InmemoryDeckListClient.shared)
        val listAllUseCase = ListAllDecksUseCase(listAllDecksRepository)
        DeckListViewModel(listAllUseCase)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDeckListBinding.bind(view)

        registerItemsInDeckList()
        registerItemsInFavoriteDeckList()

        val requestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.color.gray_100)

        Glide.with(binding.root.context)
            .load(FirebaseAuthClientProviderAdapter.shared.getUserPhotoURI())
            .apply(requestOptions)
            .into(binding.userProfileImagaViewInDeckListFragment)

        binding.recyclerDecks.adapter = deckListAdapter
        binding.recyclerDecksRecents.adapter = recentDeckListAdapter
        binding.recyclerDecksRecents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.buttonCreateDeck.setOnClickListener {
            if (binding.buttonCreateDeck.text == "criar baralho") {
                createDeckDialog = CreateDeckBottomSheetView(requireContext(), setGalleryImage = { deckImageView ->
                    val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    this.launcher.launch(pickImg)
                }, onSucess = { sucessMesage ->
                    Snackbar
                        .make(binding.root, sucessMesage, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(requireContext().getColor(R.color.green_500))
                        .show()

                    load()
                }, onFailure = { failureMesage ->
                    Snackbar
                        .make(binding.root, failureMesage, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(requireContext().getColor(R.color.red_500))
                        .show()

                    load()
                })
                createDeckDialog?.show()
            } else {
                load()
            }
        }

        binding.userProfileImagaViewInDeckListFragment.setOnClickListener {
            val action = DeckListFragmentDirections.actionDecksFragmentToProfileFragment()
            findNavController().navigate(action)
        }

        configurebindWithViewModel()
        changeStateIsLoading(true)
    }

    override fun onResume() {
        super.onResume()

        load()
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageURI = it.data?.data
            this.createDeckDialog?.deckImageUri = imageURI
        }
    }

    private fun load() {
        changeStateIsLoading(true)

        lifecycleScope.launch {
            delay(1000)

            allDecksViewModel.loadAllDecks()
            favoriteDecksListViewModel.loadAllDecks()

            changeStateIsLoading(false)
        }
    }

    private fun changeStateIsLoading(isLoading: Boolean) {
        binding.progressDeckListLoader.isVisible = isLoading

        binding.textDecksTitle.isVisible = !isLoading
        binding.textRecentsDecks.isVisible = !isLoading

        binding.buttonCreateDeck.isVisible = !isLoading

        binding.recyclerDecks.isVisible = !isLoading
        binding.recyclerDecksRecents.isVisible = !isLoading
    }

    private fun configurebindWithViewModel() {
        allDecksViewModel.decksViewData.observe(requireActivity()) { viewData ->
            viewData.decks.value?.let {
                return@observe updateWithDeck(it)
            }
            viewData.empty.value?.let {
                return@observe updateWithEmptyState(it)
            }
            viewData.error.value?.let {
                return@observe updateWithErrorState(it)
            }
        }

        favoriteDecksListViewModel.favoriteDecksViewData.observe(requireActivity()) { viewData ->
            viewData.error.value?.let {
                binding.recyclerDecksRecents.visibility = View.GONE
                binding.textRecentsDecks.visibility = View.GONE
                return@observe
            }
            viewData.empty.value?.let {
                binding.recyclerDecksRecents.visibility = View.GONE
                binding.textRecentsDecks.visibility = View.GONE
                return@observe
            }
            viewData.decks.value?.let {
                binding.recyclerDecksRecents.visibility = View.VISIBLE
                binding.textRecentsDecks.visibility = View.VISIBLE
                val viewDatas = it.map {
                    ItemViewData(FavoriteDeckItemViewHolder.IDENTIFIER, FavoriteDecksListState(it))
                }
                recentDeckListAdapter.update(viewDatas)
                return@observe
            }
        }
    }

    private fun registerItemsInFavoriteDeckList() {
        recentDeckListAdapter.register(DeckItemViewHolder.IDENTIFIER) { parent ->
            FavoriteDeckItemViewHolder.instantiate(parent) { deckViewData ->
                deckViewData.deck?.id?.let { deckId ->
                    val action = DeckListFragmentDirections.actionDecksFragmentToDeckFragment(deckId)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun registerItemsInDeckList() {
        deckListAdapter.register(DeckItemViewHolder.IDENTIFIER) { parent ->
            DeckItemViewHolder.instantiate(parent) { deckViewData ->
                deckViewData.deck?.id?.let { deckId ->
                    val action = DeckListFragmentDirections.actionDecksFragmentToDeckFragment(deckId)
                    findNavController().navigate(action)
                }
            }
        }
        deckListAdapter.register(DeckListEmptyStateItemViewHolder.IDENTIFIER) { parent ->
            DeckListEmptyStateItemViewHolder.instantiate(parent)
        }
        deckListAdapter.register(DeckListErrorStateItemViewHolder.IDENTIFIER) { parent ->
            DeckListErrorStateItemViewHolder.instantiate(parent)
        }
    }

    private fun updateWithDeck(decks: List<DeckListViewData.Deck>) {
        binding.buttonCreateDeck.text = "criar baralho"
        binding.recyclerDecks.layoutManager = GridLayoutManager(requireContext(), 2)
        val vds = decks.map { deck ->
            ItemViewData(DeckItemViewHolder.IDENTIFIER, AllDecksListState(deck = deck))
        }
        deckListAdapter.update(vds)
    }

    private fun updateWithEmptyState(empty: DeckListViewData.Empty) {
        binding.buttonCreateDeck.text = "criar baralho"
        binding.recyclerDecks.layoutManager = LinearLayoutManager(requireContext())
        val emptyViewData = ItemViewData(DeckListEmptyStateItemViewHolder.IDENTIFIER, AllDecksListState(empty = empty))
        deckListAdapter.update(listOf(emptyViewData))
    }

    private fun updateWithErrorState(error: DeckListViewData.Error) {
        binding.recyclerDecks.layoutManager = LinearLayoutManager(requireContext())
        binding.buttonCreateDeck.text = "tentar novamente"
        val emptyViewData = ItemViewData(DeckListErrorStateItemViewHolder.IDENTIFIER, AllDecksListState(error = error))
        deckListAdapter.update(listOf(emptyViewData))
    }
}

