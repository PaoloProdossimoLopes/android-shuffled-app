package com.programou.shuffled.authenticated.deck

import android.content.Context
import android.os.Bundle
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programou.shuffled.R
import com.programou.shuffled.databinding.ViewAddCardBottomSheetDialogBinding


class CreateEditCardBottomSheet(
    context: Context,
    private val cardViewData: PreviewViewData?,
    private val onDone: (PreviewViewData) -> Unit,
    private val onDelete: ((PreviewViewData) -> Unit)? = null
): BottomSheetDialog(context) {

    private lateinit var binding: ViewAddCardBottomSheetDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ViewAddCardBottomSheetDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idBtnDismiss.setOnClickListener {
            val question = binding.editTextCardQuestion.text.toString()
            val answer = binding.editTextCardAnswer.text.toString()
            val card = PreviewViewData(cardViewData?.id, question, answer)
            onDone(card)
            dismiss()
        }

        binding.deleteCardIndicatorImageView.isVisible = cardViewData?.id != null
        binding.deleteCardIndicatorImageView.setOnClickListener {
            onDelete?.invoke(cardViewData!!)
            dismiss()
        }

        binding.imageClose.setOnClickListener {
            dismiss()
        }

        binding.idBtnDismiss.setBackgroundColor(context.getColor(R.color.turquoise_500))
        binding.idBtnDismiss.setTextColor(context.getColor(R.color.white))

        cardViewData?.let { viewData ->
            binding.editTextCardQuestion.setText(viewData.question)
            binding.editTextCardAnswer.setText(viewData.anwser)
        }

        setCancelable(false)
    }
}