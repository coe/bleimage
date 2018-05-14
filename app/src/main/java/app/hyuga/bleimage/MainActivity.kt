package app.hyuga.bleimage

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import app.hyuga.bleimage.ui.main.MainFragment
import app.hyuga.bleimage.ui.main.MainHandler
import app.hyuga.bleimage.ui.scan.ScanlistFragment
import java.util.*

class MainActivity : AppCompatActivity(),MainHandler {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment = MainFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container,fragment)
                .commit()
    }

    override fun onClickScan() {
        //fragment変える
        val fragment = ScanlistFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container,fragment)
                .commit()
    }

    override fun onClickSend() {
//        Log.d(TAG,"onClickSend:"+mainObservable.imageBitmap?.byteCount)
//        val baoStream = ByteArrayOutputStream()
//        mainObservable.imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 90, baoStream)
//        baoStream.flush()
//        byteArray = baoStream.toByteArray()
//        baoStream.close()
//
//
//        byteArray?.let {
//            mOffset = 0
//            //mMtuずつ送る
//            var maxsize = it.size
//            var offset = 0
//            val b = it.get(1)
//            while (maxsize > mMtu) {
//                val arr = it.sliceArray(offset..offset+mMtu-1)
//                sendingBytesList.add(arr)
//                offset += mMtu
//                maxsize -= mMtu
//            }
//            val arr = it.sliceArray(offset..it.size-1)
//            sendingBytesList.add(arr)
//
//        }
//
//
//        //書き込む
//        val lengthCharacteristic = mGatt!!.getService(LONG_DATA_SERVICE_UUID)!!.getCharacteristic(LONG_DATA_WRITE_LENGTH_CHARACTERISTIC_UUID)
//        val length:Long = byteArray!!.size.toLong()
//        Log.d(TAG,"length:"+length)
//        val buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(length)
//        Log.d(TAG,"buffer:"+JavaUtil.tostr(buffer))
//
//        val ret = lengthCharacteristic.setValue(buffer.array())
//
//        Log.d(TAG,"descriptor ret:${ret}")
//        mGatt?.writeCharacteristic(lengthCharacteristic)
    }

    override fun onClickCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
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
