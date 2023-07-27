package com.programou.shuffled.authenticated.createDeck

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programou.shuffled.InmemoryDeckListClient
import com.programou.shuffled.R
import com.programou.shuffled.databinding.ViewCreateDeckBottomSheetDialogBinding


class CreateDeckBottomSheetView(
    context: Context,
    private val setGalleryImage: (ImageView) -> Unit,
    private val onSucess: (String) -> Unit,
    private val onFailure: (String) -> Unit
): BottomSheetDialog(context) {

    private lateinit var binding: ViewCreateDeckBottomSheetDialogBinding
    private val viewModel: CreateDeckViewModel by lazy {
        val repository = CreateDeckRepositoryImpl(InmemoryDeckListClient.shared)
        val useCase = CreateDeckUseCase(repository)
        CreateDeckViewModel(useCase)
    }

    var deckImageUri: Uri? = null
        set(value) {
            field = value
            binding.deckImageView.setImageURI(field)
        }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        binding = ViewCreateDeckBottomSheetDialogBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        viewModel.onComplete.observe(this) { isSucceed ->
            if (isSucceed) {
                onSucess("Sucesso!")
            } else {
                onFailure("Falha!")
            }
            dismiss()
        }

        binding.closeDeckCreationIndicatorImageView.setOnClickListener {
            dismiss()
        }

        binding.deckImageContainerCardView.setOnClickListener {
            setGalleryImage(binding.deckImageView)
        }

        binding.createDeckButton.setOnClickListener {
            val name = binding.deckNameEditText.text.toString()
            val description = binding.deckDescriptionEditText.text.toString()
            val viewData = CreateDeckViewDataRequest(name, description, deckImageUri.toString(), false, listOf())
            viewModel.create(viewData)

            binding.deckNameEditText.setText(String())
            binding.deckDescriptionEditText.setText(String())
            binding.deckImageView.setImageURI(null)
        }

        binding.createDeckButton.setBackgroundColor(context.getColor(R.color.turquoise_500))
        binding.createDeckButton.setTextColor(context.getColor(R.color.white))

        setCancelable(false)
    }
}