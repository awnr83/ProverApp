package com.moapdev.proverapp

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("loadImage")
fun loadImage(imgView: ImageView, imgUrl: String){
    imgUrl?.let {
        Glide.with(imgView.context)
            .load(it)
            .apply(RequestOptions()
                .placeholder(R.drawable.animation_load)
                .error(R.drawable.ic_broken))
            .into(imgView)
    }
}
