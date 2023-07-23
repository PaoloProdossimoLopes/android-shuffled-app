package com.programou.shuffled.authenticated.deckList

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.programou.shuffled.InmemoryDeckListClient
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewData
import com.programou.shuffled.authenticated.ListAdapter
import com.programou.shuffled.authenticated.createDeck.CreateDeckBottomSheetView
import com.programou.shuffled.databinding.FragmentDeckListBinding
import kotlinx.coroutines.Dispatchers
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

        binding.recyclerDecks.adapter = deckListAdapter
        binding.recyclerDecksRecents.adapter = recentDeckListAdapter
        binding.recyclerDecksRecents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.buttonCreateDeck.setOnClickListener {
            if (binding.buttonCreateDeck.text == "criar baralho") {
                val dialog = CreateDeckBottomSheetView(requireContext())
                dialog.show()
            } else {
                load()
            }
        }

        configurebindWithViewModel()

        load()
    }

    private fun load() {
        lifecycleScope.launch {
            changeStateIsLoading(true)
            updateMock()
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
            viewData.decks.value?.let {
                val viewDatas = it.map { ItemViewData(FavoriteDeckItemViewHolder.IDENTIFIER, FavoriteDecksListState(it)) }
                recentDeckListAdapter.update(viewDatas)
                return@observe
            }
            viewData.empty.value?.let {
                binding.recyclerDecksRecents.isVisible = false
                return@observe
            }
            viewData.error.value?.let {
                binding.recyclerDecksRecents.isVisible = false
                return@observe
            }
        }
    }

    private fun registerItemsInFavoriteDeckList() {
        recentDeckListAdapter.register(DeckItemViewHolder.IDENTIFIER) { parent ->
            FavoriteDeckItemViewHolder.instantiate(parent) {
                val action = DeckListFragmentDirections.actionDecksFragmentToDeckFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun registerItemsInDeckList() {
        deckListAdapter.register(DeckItemViewHolder.IDENTIFIER) { parent ->
            DeckItemViewHolder.instantiate(parent) {
                val action = DeckListFragmentDirections.actionDecksFragmentToDeckFragment()
                findNavController().navigate(action)
            }
        }
        deckListAdapter.register(DeckListEmptyStateItemViewHolder.IDENTIFIER) { parent ->
            DeckListEmptyStateItemViewHolder.instantiate(parent)
        }
        deckListAdapter.register(DeckListErrorStateItemViewHolder.IDENTIFIER) { parent ->
            DeckListErrorStateItemViewHolder.instantiate(parent)
        }
    }

    private suspend fun updateMock() {
        withContext(Dispatchers.IO) {
            allDecksViewModel.loadAllDecks()
            favoriteDecksListViewModel.loadAllDecks()

            delay(2000)

            withContext(Dispatchers.Main) {
                changeStateIsLoading(false)
            }
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

