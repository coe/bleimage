package app.hyuga.bleimage.ui

import android.bluetooth.BluetoothDevice
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.graphics.Bitmap
import android.net.Uri
import app.hyuga.bleimage.BR

class ConnectedDeviceObservable : BaseObservable() {

    @get:Bindable
    var connectedDeviceName: String? = null
        set(value) {
            field = value  // 値をセット
            notifyPropertyChanged(BR.connectedDeviceName) // 変更を通知
        }

}