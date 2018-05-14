package app.hyuga.bleimage

import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.ImageView

object ImageViewBindingAdapter {
    @BindingAdapter("bind:imageUri")
    @JvmStatic
    fun loadImage(view: ImageView, uri: Uri?) {
        view.setImageURI(uri)
    }

    @BindingAdapter("bind:imageBitmap")
    @JvmStatic
    fun loadImageBitmap(view: ImageView, bitmap: Bitmap?) {
        Log.d("ImageViewBindingAdapter","loadImageBitmap")
        view.setImageBitmap(bitmap)
    }
}