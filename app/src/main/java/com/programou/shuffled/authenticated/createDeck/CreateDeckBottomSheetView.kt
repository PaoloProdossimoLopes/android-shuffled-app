package com.programou.shuffled.authenticated.createDeck

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programou.shuffled.R
import com.programou.shuffled.databinding.ViewCreateDeckBottomSheetDialogBinding

class CreateDeckBottomSheetView(context: Context): BottomSheetDialog(context) {

    private lateinit var binding: ViewCreateDeckBottomSheetDialogBinding

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        binding = ViewCreateDeckBottomSheetDialogBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        binding.closeDeckCreationIndicatorImageView.setOnClickListener {
            dismiss()
        }
        binding.createDeckButton.setOnClickListener {
            dismiss()
        }

        binding.createDeckButton.setBackgroundColor(context.getColor(R.color.turquoise_500))
        binding.createDeckButton.setTextColor(context.getColor(R.color.white))

        setCancelable(false)
    }
}