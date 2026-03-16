package com.ofek.hunter.utilities

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * Load images from URLs using Glide with caching.
 */
object ImageLoader {

    // Load an image cropped into a circle (for avatars etc.)
    fun loadCircleImage(imageView: ImageView, url: String) {
        Glide.with(imageView.context)
            .load(url)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }
}
