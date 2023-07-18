package com.programou.shuffled.authenticated.deckList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.programou.shuffled.InmemoryDeckListClient
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewData
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.authenticated.ListAdapter
import com.programou.shuffled.databinding.FragmentDeckListBinding
import com.programou.shuffled.databinding.ViewDeckListEmptyStateItemBinding
import com.programou.shuffled.databinding.ViewDeckListItemBinding
import com.programou.shuffled.databinding.ViewFavoriteDeckListItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeckListFragment : Fragment(R.layout.fragment_deck_list) {

    private lateinit var binding: FragmentDeckListBinding
    private val deckListAdapter = ListAdapter<DeckListItemViewData>()
    private val recentDeckListAdapter = ListAdapter<DeckListFavoriteItemViewData>()

    private val viewModel: DeckListViewModel by lazy {
        val listAllDecksRepository = RemoteListAllDeckRepository(InmemoryDeckListClient.shared)
        val listAllUseCase = ListAllDecksUseCase(listAllDecksRepository)
        DeckListViewModel(listAllUseCase)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDeckListBinding.bind(view)

        registerItemsInDeckList()
        registerItemsInFavoriteDeckList()
        configureInitialLayoutState()

        configurebindWithViewModel()

        lifecycleScope.launch {
            updateMock()
        }

        viewModel.loadAllDecks()
    }

    private fun configurebindWithViewModel() {
        viewModel.decksViewData.observe(requireActivity()) { viewData ->
            viewData.decks.value?.let { return@observe updateWithDeck(it) }
            viewData.empty.value?.let { return@observe updateWithEmptyState(it) }
        }
    }

    private fun configureInitialLayoutState() {
        binding.progressDeckListLoader.visibility = View.VISIBLE

        binding.textDecksTitle.visibility = View.GONE
        binding.textRecentsDecks.visibility = View.GONE

        binding.buttonCreateDeck.visibility = View.GONE

        binding.recyclerDecks.visibility = View.GONE
        binding.recyclerDecks.adapter = deckListAdapter
        binding.recyclerDecks.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.recyclerDecksRecents.visibility = View.GONE
        binding.recyclerDecksRecents.adapter = recentDeckListAdapter
        binding.recyclerDecksRecents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
            DeckListEmptyStateItemViewHolder.instantiate(parent) {

            }
        }
    }

    private fun updateWithDeck(decks: List<DeckListViewData.Deck>) {
        val vds = decks.map { deck ->
            ItemViewData(DeckItemViewHolder.IDENTIFIER, DeckListItemViewData(deck = deck))
        }
        deckListAdapter.update(vds)
    }

    private fun updateWithEmptyState(empty: DeckListViewData.Empty) {
        this.binding.recyclerDecks.layoutManager = LinearLayoutManager(requireContext())
        val emptyViewData = ItemViewData(DeckListEmptyStateItemViewHolder.IDENTIFIER, DeckListItemViewData(empty = empty))
        deckListAdapter.update(listOf(emptyViewData))
    }

    private suspend fun updateMock() {
        withContext(Dispatchers.IO) {
            delay(3000)

            val elements = listOf(
                DeckListViewData.Deck(1, "Ingles", "32", "https://s4.static.brasilescola.uol.com.br/be/2022/05/bandeira-dos-estados-unidos.jpg"),
                DeckListViewData.Deck(2, "Frances", "100",  "https://www.eurodicas.com.br/wp-content/uploads/2018/10/bandeira-da-franca-1200x900.jpg"),
                DeckListViewData.Deck(3, "Jappones", "1", "https://ichef.bbci.co.uk/news/1024/branded_portuguese/135A8/production/_110227297_gettyimages-512612394.jpg")
            )
            val viewDatas = elements.map { deck ->
                ItemViewData(FavoriteDeckItemViewHolder.IDENTIFIER, DeckListFavoriteItemViewData(deck = deck))
            }

            withContext(Dispatchers.Main) {
                recentDeckListAdapter.update(viewDatas)

                binding.progressDeckListLoader.visibility = View.GONE

                binding.buttonCreateDeck.visibility = View.VISIBLE

                binding.textDecksTitle.visibility = View.VISIBLE
                binding.textRecentsDecks.visibility = View.VISIBLE

                binding.recyclerDecks.visibility = View.VISIBLE
                binding.recyclerDecksRecents.visibility = View.VISIBLE
            }
        }
    }
}

class DeckListItemViewData (
    var deck: DeckListViewData.Deck? = null,
    var empty: DeckListViewData.Empty? = null
)

class DeckListFavoriteItemViewData (
    var deck: DeckListViewData.Deck? = null,
)

