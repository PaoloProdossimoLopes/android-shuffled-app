package com.programou.shuffled.authenticated.deckList

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.programou.shuffled.R
import com.programou.shuffled.authenticated.ItemViewHolder
import com.programou.shuffled.databinding.ViewDeckListItemBinding
import kotlinx.coroutines.async


class DeckItemViewHolder private constructor(
    private val activity: FragmentActivity,
    private val binding: ViewDeckListItemBinding,
    private val imageLoader: LoadImage,
    private val onClick: Bind<DeckListFragment.AllDecksListState>?
): ItemViewHolder<DeckListFragment.AllDecksListState>(binding.root) {
    companion object {
        val IDENTIFIER: Int by lazy { DeckItemViewHolder.hashCode() }

        fun instantiate(activity: FragmentActivity, parent: ViewGroup, imageLoader: LoadImage, onClick: Bind<DeckListFragment.AllDecksListState>?): DeckItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewDeckListItemBinding.inflate(inflater, parent, false)

            return DeckItemViewHolder(activity, binding, imageLoader, onClick)
        }
    }

    override fun bind(viewData: DeckListFragment.AllDecksListState) {
        with(viewData.deck!!) {
            binding.textDeckTitle.text = name
            binding.totalOfCardsTextViewInDeckListItemView.text = numberOfCards

            activity.lifecycleScope.async {
                val uri = thumbnailUrl.toUri()
                imageLoader.loadFrom(uri, binding.imageDeck, binding.root.context)
            }
        }

        binding.cardContainer.setOnClickListener {
            onClick?.let{ it(viewData) }
        }
    }
}
interface LoadImage {
    suspend fun loadFrom(uri: Uri?, into: ImageView, context: Context)
}

class GlideImageLoaderAdapter: LoadImage {
    override suspend fun loadFrom(uri: Uri?, into: ImageView, context: Context) {
        val requestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.color.gray_100)

        Glide.with(context)
            .load(uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(requestOptions)
            .into(into)
    }
}
