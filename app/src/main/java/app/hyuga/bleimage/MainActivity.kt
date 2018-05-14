package app.hyuga.bleimage

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelUuid
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import app.hyuga.bleimage.ui.main.MainFragment
import app.hyuga.bleimage.ui.main.MainHandler
import app.hyuga.bleimage.ui.receivedimage.ReceivedImageFragment
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class MainActivity : AppCompatActivity(), MainFragment.OnListFragmentInteractionListener {
    private var byteArray:ByteArray? = null
    private var mMtu = 32
    private var sendingBytesList:LinkedList<ByteArray> = LinkedList()
    private var mGatt:BluetoothGatt? = null

    override fun onClickScan() {

    }

    override fun onClickSend(bitmap: Bitmap) {
        val baoStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baoStream)
        baoStream.flush()
        byteArray = baoStream.toByteArray()
        baoStream.close()


        byteArray?.let {
            //mMtuずつ送る
            var maxsize = it.size
            var offset = 0
            val b = it.get(1)
            while (maxsize > mMtu) {
                val arr = it.sliceArray(offset..offset+mMtu-1)
                sendingBytesList.add(arr)
                offset += mMtu
                maxsize -= mMtu
            }
            val arr = it.sliceArray(offset..it.size-1)
            sendingBytesList.add(arr)

        }

        //書き込む
        val lengthCharacteristic = mGatt!!.getService(LONG_DATA_SERVICE_UUID)!!.getCharacteristic(LONG_DATA_WRITE_LENGTH_CHARACTERISTIC_UUID)
        val length:Long = byteArray!!.size.toLong()
        Log.d(TAG,"length:"+length)
        val buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(length)

        val ret = lengthCharacteristic.setValue(buffer.array())

        Log.d(TAG,"descriptor ret:${ret}")
        mGatt?.writeCharacteristic(lengthCharacteristic)
    }

    private var bluetoothGattServer: BluetoothGattServer? = null

    private var maxDataSize = 0

    private var receiverdData:ByteBuffer? = null

    private val mBluetoothGattServerCallback: BluetoothGattServerCallback = object : BluetoothGattServerCallback() {

        override fun onMtuChanged(device: BluetoothDevice?, mtu: Int) {
            Log.d(TAG,"onMtuChanged:${mtu}")
            super.onMtuChanged(device, mtu)
        }

        override fun onCharacteristicWriteRequest(device: BluetoothDevice?, requestId: Int, characteristic: BluetoothGattCharacteristic?, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray?) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
            Log.d(TAG,"onCharacteristicWriteRequest count:"+value?.size)

            when(characteristic?.uuid) {
                LONG_DATA_WRITE_LENGTH_CHARACTERISTIC_UUID -> {
                    Log.d(TAG,"onCharacteristicWriteRequest count:"+value?.size)
                    val v = value
                    maxDataSize = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getLong().toInt()
                    receiverdData = null
                    Log.d(TAG,"onCharacteristicWriteRequest size:"+maxDataSize)


                }
                LONG_DATA_WRITE_CHARACTERISTIC_UUID -> {
                    Log.d(TAG,"onCharacteristicWriteRequest size:"+maxDataSize)

                    if (receiverdData == null) {
                        receiverdData = ByteBuffer.allocate(maxDataSize)
                    }
                    receiverdData?.put(value)
                    val limit = receiverdData?.remaining()
                    Log.d(TAG,"onCharacteristicWriteRequest limit:"+limit)
                    if (limit == 0) {

                        val byteArray = receiverdData?.array()
                        val file = File.createTempFile("temp","jpg")
                        file.writeBytes(byteArray!!)
                        val uri = Uri.fromFile(file)
                        val fragment = ReceivedImageFragment.newInstance(uri)
                        supportFragmentManager.beginTransaction().replace(R.id.container,fragment).commit()
                    }
                }
            }
            bluetoothGattServer?.sendResponse(device,requestId, BluetoothGatt.GATT_SUCCESS,offset, value)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.container,MainFragment.newInstance()).commit()

        //peripheral
        val manager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter.bluetoothLeAdvertiser?.let {
            bluetoothGattServer = manager.openGattServer(this,mBluetoothGattServerCallback)

            val service = BluetoothGattService(
                    LONG_DATA_SERVICE_UUID,BluetoothGattService.SERVICE_TYPE_PRIMARY
            )
            service.addCharacteristic(BluetoothGattCharacteristic(LONG_DATA_WRITE_CHARACTERISTIC_UUID,BluetoothGattCharacteristic.PROPERTY_WRITE,BluetoothGattCharacteristic.PERMISSION_WRITE))
            service.addCharacteristic(BluetoothGattCharacteristic(LONG_DATA_WRITE_LENGTH_CHARACTERISTIC_UUID,BluetoothGattCharacteristic.PROPERTY_WRITE,BluetoothGattCharacteristic.PERMISSION_WRITE))

            bluetoothGattServer?.addService(service)
            //アドバタイジング開始
            val parcelUuid = ParcelUuid(LONG_DATA_SERVICE_UUID)
            val settings = AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                    .setConnectable(true)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .build()

            val advertiseData = AdvertiseData.Builder()
                    .addServiceUuid(parcelUuid)
                    .setIncludeDeviceName(true)
                    .build()

            manager.adapter.bluetoothLeAdvertiser?.startAdvertising(settings,advertiseData,object : AdvertiseCallback(){

            })
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
