package com.programou.shuffled

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

interface LoadImage {
    fun loadFrom(uri: Uri?, into: ImageView, context: Context)
}

class GlideImageLoaderAdapter private constructor(): LoadImage {
    companion object {
        val shared = GlideImageLoaderAdapter()
    }
    override fun loadFrom(uri: Uri?, into: ImageView, context: Context) {
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
