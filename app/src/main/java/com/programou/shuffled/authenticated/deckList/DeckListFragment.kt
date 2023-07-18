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

        recentDeckListAdapter.register(DeckItemViewHolder.IDENTIFIER) { parent ->
            FavoriteDeckItemViewHolder.instantiate(parent) {
                val action = DeckListFragmentDirections.actionDecksFragmentToDeckFragment()
                findNavController().navigate(action)
            }
        }

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

        lifecycleScope.launch {
            updateMock()
        }

        viewModel.decksViewData.observe(requireActivity()) { viewData ->
            var itens = mutableListOf<ItemViewData<DeckListItemViewData>>()

            viewData.decks.on { decks ->
                decks.forEach { deck ->
                    val vd = ItemViewData(DeckItemViewHolder.IDENTIFIER, DeckListItemViewData(onDeck = { binding ->
                        binding.textDeckTitle.text = deck.name
                        binding.tvTotalCards.text = deck.numberOfCards

                        val requestOptions = RequestOptions()
                            .centerCrop()
                            //.error(R.drawable.ic_no_image)
                            .placeholder(R.color.gray_100)

                        Glide.with(binding.root.context)
                            .load(deck.thumbnailUrl)
                            .apply(requestOptions)
                            .into(binding.imageDeck)
                    }))
                    itens.add(vd)
                }
            }

            viewData.empty.on { empty ->
                this.binding.recyclerDecks.layoutManager = LinearLayoutManager(requireContext())
                val vd = ItemViewData(DeckListEmptyStateItemViewHolder.IDENTIFIER, DeckListItemViewData(onEmpty = { binding ->
                    binding.tvTitle.text = empty.title
                    binding.tvDescription.text = empty.message
                }))
                itens.add(vd)
            }

            deckListAdapter.update(itens)
        }

        viewModel.loadAllDecks()
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
                ItemViewData(FavoriteDeckItemViewHolder.IDENTIFIER, DeckListFavoriteItemViewData { binding ->
                    binding.textDeckTitle.text = deck.name
                    binding.tvTotalCards.text = deck.numberOfCards

                    val requestOptions = RequestOptions()
                        .centerCrop()
                        //.error(R.drawable.ic_no_image)
                        .placeholder(R.color.gray_100)

                    Glide.with(binding.root.context)
                        .load(deck.thumbnailUrl)
                        .apply(requestOptions)
                        .into(binding.imageDeck)
                })
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
    var onDeck: Bind<ViewDeckListItemBinding>? = null,
    var onEmpty: Bind<ViewDeckListEmptyStateItemBinding>? = null
)

class DeckListFavoriteItemViewData (
    var onDeck: Bind<ViewFavoriteDeckListItemBinding>? = null,
)


typealias Bind<T> = (T) -> Unit

class DeckItemViewHolder private constructor(private val binding: ViewDeckListItemBinding, private val onClick: Bind<DeckListItemViewData>?): ItemViewHolder<DeckListItemViewData>(binding.root) {
    companion object {
        val IDENTIFIER: Int by lazy { DeckItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup, onClick: Bind<DeckListItemViewData>?): DeckItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckListItemBinding.inflate(inflater, parent, false)

            return DeckItemViewHolder(binding, onClick)
        }
    }

    override fun bind(viewData: DeckListItemViewData) {
        viewData.onDeck?.let { it(binding) }

        binding.cardContainer.setOnClickListener {
            onClick?.let{ it(viewData) }
        }
    }

}

class DeckListEmptyStateItemViewHolder private constructor(private val binding: ViewDeckListEmptyStateItemBinding, private val onClick: Bind<DeckListItemViewData>?): ItemViewHolder<DeckListItemViewData>(binding.root) {
    companion object {
        val IDENTIFIER: Int by lazy { DeckListEmptyStateItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup, onClick: Bind<DeckListItemViewData>?): DeckListEmptyStateItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckListEmptyStateItemBinding.inflate(inflater, parent, false)

            return DeckListEmptyStateItemViewHolder(binding, onClick)
        }
    }

    override fun bind(viewData: DeckListItemViewData) {
        viewData.onEmpty?.let { it(binding) }
    }
}

class FavoriteDeckItemViewHolder private constructor(private val binding: ViewFavoriteDeckListItemBinding, private val onClick: Bind<DeckListFavoriteItemViewData>?): ItemViewHolder<DeckListFavoriteItemViewData>(binding.root) {
    companion object {
        val IDENTIFIER: Int by lazy { DeckItemViewHolder.hashCode() }

        fun instantiate(parent: ViewGroup, onClick: Bind<DeckListFavoriteItemViewData>?): FavoriteDeckItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewFavoriteDeckListItemBinding.inflate(inflater, parent, false)

            return FavoriteDeckItemViewHolder(binding, onClick)
        }
    }

    override fun bind(viewData: DeckListFavoriteItemViewData) {
        viewData.onDeck?.let { it(binding) }

        binding.cardContainer.setOnClickListener {
            onClick?.let{ it(viewData) }
        }
    }
}

