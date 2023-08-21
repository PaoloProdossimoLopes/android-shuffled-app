package com.programou.shuffled.home.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.programou.shuffled.GlideImageLoaderAdapter
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.createDeck.CreateDeckBottomSheetView
import com.programou.shuffled.database.ShuffledDatabase
import com.programou.shuffled.databinding.FragmentHomeBinding
import com.programou.shuffled.databinding.ViewDeckListEmptyStateItemBinding
import com.programou.shuffled.databinding.ViewDeckListErrorStateItemBinding
import com.programou.shuffled.databinding.ViewDeckListItemBinding
import com.programou.shuffled.home.data.DeckRepositoryAdapter
import com.programou.shuffled.home.domain.DeckViewData
import com.programou.shuffled.home.domain.HomeEmptyStateViewData
import com.programou.shuffled.home.domain.HomeErrorStateViewData
import com.programou.shuffled.home.domain.LoadAllDecks
import com.programou.shuffled.home.presenter.HomeViewModel
import com.programou.shuffled.home.presenter.ResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val iamgeLoader = GlideImageLoaderAdapter.shared
    private val deckListAdapter = HomeDeckRecyclerViewAdapter()
    private var createDeckDialog: CreateDeckBottomSheetView? = null
    private val viewModel: HomeViewModel by viewModels {
        val resourceProvider = object: ResourceProvider {
            override fun string(id: Int) = getString(id)
        }
        val deckRespository = DeckRepositoryAdapter(
            ShuffledDatabase.getDatabase(requireContext())
        )
        val decksLoader = LoadAllDecks(deckRespository)
        HomeViewModel.Factory(resourceProvider, decksLoader)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageURI = it.data?.data
            this.createDeckDialog?.deckImageUri = imageURI
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        registerViewHolders()
        binding.homeDecksListRecyclerView.adapter = deckListAdapter

        bindViewModelObserver()
        binding.homeCreateDeckButton.setOnClickListener { onCreateDidClicked() }
    }

    override fun onResume() {
        super.onResume()
        binding.homeScreenTitleTextView.text = viewModel.getHomeTitle()
        onBecameVisible()
    }

    private fun onBecameVisible() {
        lifecycleScope.launch {
            viewModel.onBacameVisible()
        }
    }

    private fun registerViewHolders() {
        deckListAdapter.register(DeckItemViewHolder.Identifier) { parent ->
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckListItemBinding.inflate(inflater, parent, false)
            DeckItemViewHolder(binding, iamgeLoader, onClick = { viewData ->
                val action = HomeFragmentDirections.actionHomeFragmentToDeckFragmen2t(viewData.id)
                findNavController().navigate(action)
            })
        }
        deckListAdapter.register(HomeEmptyStateViewHolder.identifier) { parent ->
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckListEmptyStateItemBinding.inflate(inflater, parent, false)
            HomeEmptyStateViewHolder(binding)
        }
        deckListAdapter.register(HomeErrorStateViewHolder.identifier) { parent ->
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckListErrorStateItemBinding.inflate(inflater, parent, false)
            HomeErrorStateViewHolder(binding, onClick = { onBecameVisible() })
        }
    }

    private fun onDisplayLoader(isLoading: Boolean) {
        binding.homeLoaderProgressBar.isActivated = isLoading
        binding.homeLoaderProgressBar.isVisible = isLoading
        binding.homeDecksListRecyclerView.isVisible = !isLoading
    }

    private fun onDisplayDecks(decksViewData: List<DeckViewData>) {
        val deckItemViewDatas = decksViewData.map { viewData ->
            val vd = DeckItemViewHolder.ViewData(
                viewData.id,
                viewData.deckName,
                viewData.imageUrl,
                viewData.numberOfFlashcards
            )
            DeckItemViewHolderController(vd)
        }
        binding.homeDecksListRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        deckListAdapter.update(deckItemViewDatas)
    }

    private fun onDisplayError(errorViewData: HomeErrorStateViewData) {
        val errorViewData =
            HomeErrorStateViewHolder.ViewData(errorViewData.title, errorViewData.message)
        val errorController = HomeErrorStateViewHolderController(errorViewData)
        binding.homeDecksListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        deckListAdapter.update(listOf(errorController))
    }

    private fun onDisplayEmpty(emptyViewData: HomeEmptyStateViewData) {
        val viewHolderViewData =
            HomeEmptyStateViewHolder.ViewData(emptyViewData.title, emptyViewData.message)
        val emptyController = HomeEmptyStateViewHolderController(viewHolderViewData)
        binding.homeDecksListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        deckListAdapter.update(listOf(emptyController))
    }

    private fun onCreateDidClicked() {
        createDeckDialog = CreateDeckBottomSheetView(
            requireContext(),
            setGalleryImage = { launchImagePicker() },
            onSucess = { sucessMesage -> onCreateDeckWithSuccess(sucessMesage) },
            onFailure = { failureMesage -> onCerateDeckWithFailure(failureMesage) }
        )
        createDeckDialog?.show()
    }

    private fun launchImagePicker() {
        val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        this.launcher.launch(pickImg)
    }

    private fun onCreateDeckWithSuccess(message: String) {
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(requireContext().getColor(R.color.green_500))
            .show()

        onBecameVisible()
    }

    private fun onCerateDeckWithFailure(message: String) {
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(requireContext().getColor(R.color.red_500))
            .show()

        onBecameVisible()
    }

    private fun bindViewModelObserver() {
        viewModel.displayLoader.observe(requireActivity()) { onDisplayLoader(it) }
        viewModel.displayDecks.observe(requireActivity()) { onDisplayDecks(it) }
        viewModel.displayError.observe(requireActivity()) { onDisplayError(it) }
        viewModel.displayEmpty.observe(requireActivity()) { onDisplayEmpty(it) }
    }
}