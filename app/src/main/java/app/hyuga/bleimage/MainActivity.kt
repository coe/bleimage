package app.hyuga.bleimage

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelUuid
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import app.hyuga.bleimage.ui.main.MainFragment
import app.hyuga.bleimage.ui.main.MainHandler
import app.hyuga.bleimage.ui.receivedimage.ReceivedImageFragment
import app.hyuga.bleimage.ui.scan.ScanlistFragment
import app.hyuga.bleimage.ui.scan.ScanlistHandler
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class MainActivity : AppCompatActivity(), MainFragment.OnListFragmentInteractionListener, ScanlistHandler {


    private var byteArray:ByteArray? = null
    private var mMtu = 32
    private var sendingBytesList:LinkedList<ByteArray> = LinkedList()
    private var mGatt:BluetoothGatt? = null

    override fun onClickScanList(scanList: Parcelable) {
        Log.d(TAG,"onClickScanList")
        supportFragmentManager.popBackStack()
        val scanResult:ScanResult = scanList as ScanResult
        scanResult.device.connectGatt(this,false,object : BluetoothGattCallback(){
            override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                Log.d(TAG,"onCharacteristicWrite:")
                super.onCharacteristicWrite(gatt, characteristic, status)

                if (sendingBytesList.size == 0) {
                    //512バイトまでしか送れないっぽい
                    Log.d(TAG,"終了:")
                } else  {
                    val datas = sendingBytesList.poll()
                    Log.d(TAG,"onCharacteristicWrite:"+datas.size)
                    val lengthCharacteristic = gatt!!.getService(LONG_DATA_SERVICE_UUID)!!.getCharacteristic(LONG_DATA_WRITE_CHARACTERISTIC_UUID)

                    lengthCharacteristic?.setValue(datas)
                    gatt.writeCharacteristic(lengthCharacteristic!!)
                }

            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                Log.d(TAG,"onServicesDiscovered")
                super.onServicesDiscovered(gatt, status)
                mGatt = gatt
            }

            override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
                Log.d(TAG,"onMtuChanged:"+mtu)
                mMtu = mtu-5
                gatt?.discoverServices()

                super.onMtuChanged(gatt, mtu, status)
            }

            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                Log.d(TAG,"onConnectionStateChange:"+newState)
                super.onConnectionStateChange(gatt, status, newState)
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        //onServicesDiscoveredに移行
                        gatt?.requestMtu(MTU)


                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                    }
                    BluetoothProfile.STATE_CONNECTING -> {
                    }
                    BluetoothProfile.STATE_DISCONNECTING -> {

                    }
                }
            }
        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.container,MainFragment.newInstance()).addToBackStack(null).commit()

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

    override fun onStart() {
        super.onStart()
        val permissioncheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissioncheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                AlertDialog.Builder(this)
                        .setTitle(R.string.no_permission)
                        .setPositiveButton(android.R.string.ok, { _,_ ->
                            finish()
                        })
                        .show()
            } else {
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_REQUEST)
            }
        }
    }

    override fun onClickScan() {
        supportFragmentManager.beginTransaction().replace(R.id.container,ScanlistFragment.newInstance()).addToBackStack(null).commit()

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
                        supportFragmentManager.beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit()
                    }
                }
            }
            bluetoothGattServer?.sendResponse(device,requestId, BluetoothGatt.GATT_SUCCESS,offset, value)
        }

    }


    companion object {
        private const val MTU = 517

        val LONG_DATA_SERVICE_UUID = UUID.fromString("D096F3C2-5148-410A-BA6A-20FEAD00D7CA")
        val LONG_DATA_WRITE_CHARACTERISTIC_UUID = UUID.fromString("E053BD84-1E5B-4A6C-AD49-C672A737880C")
        private val LONG_DATA_WRITE_LENGTH_CHARACTERISTIC_UUID = UUID.fromString("C4BDAB8A-BAC1-477A-925C-E1665553953C")

        private val PERMISSION_REQUEST = 1

        private val REQUEST_IMAGE_CAPTURE = 1
        private val REQUEST_SCAN_LIST = 2

        private const val TAG = "MainActivity"

    }
}
