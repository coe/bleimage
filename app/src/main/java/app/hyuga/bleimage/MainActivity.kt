package app.hyuga.bleimage

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_camera -> {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
            R.id.menu_scan -> {

            }
            R.id.menu_send -> {
//                Log.d(TAG,"onClickSend:"+mainObservable.imageBitmap?.byteCount)
//                val baoStream = ByteArrayOutputStream()
//                mainObservable.imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 90, baoStream)
//                baoStream.flush()
//                byteArray = baoStream.toByteArray()
//                baoStream.close()
//
//
//                byteArray?.let {
//                    mOffset = 0
//                    //mMtuずつ送る
//                    var maxsize = it.size
//                    var offset = 0
//                    val b = it.get(1)
//                    while (maxsize > mMtu) {
//                        val arr = it.sliceArray(offset..offset+mMtu-1)
//                        sendingBytesList.add(arr)
//                        offset += mMtu
//                        maxsize -= mMtu
//                    }
//                    val arr = it.sliceArray(offset..it.size-1)
//                    sendingBytesList.add(arr)
//
//                }
//
////                mGatt?.beginReliableWrite()
//
//                //書き込む
//                val lengthCharacteristic = mGatt!!.getService(LONG_DATA_SERVICE_UUID)!!.getCharacteristic(LONG_DATA_WRITE_LENGTH_CHARACTERISTIC_UUID)
//                val length:Long = byteArray!!.size.toLong()
//                Log.d(TAG,"length:"+length)
//                val buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(length)
//                Log.d(TAG,"buffer:"+JavaUtil.tostr(buffer))
//
//                val ret = lengthCharacteristic.setValue(buffer.array())
//
//                Log.d(TAG,"descriptor ret:${ret}")
//                mGatt?.writeCharacteristic(lengthCharacteristic)



            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val LONG_DATA_SERVICE_UUID = UUID.fromString("D096F3C2-5148-410A-BA6A-20FEAD00D7CA")
        val LONG_DATA_WRITE_CHARACTERISTIC_UUID = UUID.fromString("E053BD84-1E5B-4A6C-AD49-C672A737880C")
        private val LONG_DATA_WRITE_LENGTH_CHARACTERISTIC_UUID = UUID.fromString("C4BDAB8A-BAC1-477A-925C-E1665553953C")

        private val PERMISSION_REQUEST = 1

        private val REQUEST_IMAGE_CAPTURE = 1
        private val REQUEST_SCAN_LIST = 2

        private val TAG = "MainActivity"

    }
}
