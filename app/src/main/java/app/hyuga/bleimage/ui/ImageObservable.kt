package app.hyuga.bleimage.ui

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.graphics.Bitmap
import android.net.Uri
import app.hyuga.bleimage.BR

class ImageObservable : BaseObservable() {
    @get:Bindable
    var imageUri: Uri? = null
        set(value) {
            field = value  // 値をセット
            notifyPropertyChanged(BR.imageUri) // 変更を通知
        }

    @get:Bindable
    var imageBitmap: Bitmap? = null
        set(value) {
            field = value  // 値をセット
            notifyPropertyChanged(BR.imageBitmap) // 変更を通知
        }
}