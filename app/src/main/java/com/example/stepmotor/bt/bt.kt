package com.example.stepmotor.bt

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID



class bt {

    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter

    enum class BTstatus {
        DISCONNECT, CONNECTING, CONNECTED, RECEIVE
    }

    var btStatus = BTstatus.DISCONNECT

    var btIsConnected by mutableStateOf(false)

    var btIsReady by mutableStateOf(false)

    private var stm32device: BluetoothDevice? = null

    private var mSocket: BluetoothSocket? = null
    private val uuid: String = "00001101-0000-1000-8000-00805F9B34FB"


    fun init(context: Context)
    {
        bluetoothManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(BluetoothManager::class.java)
        } else {
            ContextCompat.getSystemService( context, BluetoothManager::class.java)!!
        }
        bluetoothAdapter = bluetoothManager.adapter
    }

    @SuppressLint("MissingPermission")
    fun getPairedDevices() {

        /**
         *  Получить список связанных устройств
         */
        val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices

        pairedDevices.forEach {
            println(it.name)
            println(it.address)
            it.uuids.forEach { u ->
                println(u.toString())
            }
        }

        try {
            stm32device = pairedDevices.first { it.name == "Generator" }
        }
        catch (e : NoSuchElementException)
        {
            Timber.e("Блютус устройство 'Generator' не найдено")
        }

    }

    private fun connect(context: Context) {

        if (bluetoothAdapter.isEnabled and (stm32device != null))
        {
            btStatus = BTstatus.CONNECTING
            val device = stm32device //bluetoothAdapter.getRemoteDevice(mac)
            connectScope(context, device!!)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun autoconnect(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                delay(1000)
                if (btStatus == BTstatus.DISCONNECT) {
                    connect(context)
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun connectScope(context: Context, device: BluetoothDevice) {
        GlobalScope.launch(Dispatchers.IO) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT ) == PackageManager.PERMISSION_GRANTED ) {

                try {
                    mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
                    Timber.i("Подключение...")
                    mSocket?.connect()
                    Timber.i("Подключились к устройству")
                    btIsConnected = true
                    btStatus = BTstatus.CONNECTED
                    receiveScope()
                } catch (e: IOException) {
                    btIsConnected = false
                    mSocket?.close()
                    Timber.e("Не смогли подключиться к устройсву ${e.message}")
                    btStatus = BTstatus.DISCONNECT
                }
            }

        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun receiveScope() {
        btStatus = BTstatus.RECEIVE
        var inStream: InputStream? = null
        var outStream: OutputStream? = null

        try {
            inStream = mSocket?.inputStream
            outStream = mSocket?.outputStream
        } catch (e: IOException) {
            Timber.e("Ошибка создания inputStream")
        }

        val buf = inStream?.reader(Charsets.UTF_8)?.buffered(1024 * 1024 * 8)

        var isExit = false

        GlobalScope.launch(Dispatchers.IO) {

            while (true) {
                try {
                    delay(1000)
                    outStream?.write(1)
                } catch (e: IOException) {
                    isExit = true
                    break
                }
            }

        }

        GlobalScope.launch(Dispatchers.IO) {

            while (true) {
                try {
                    if (isExit) throw IOException("Произошла ошибка ввода-вывода")
                    if (buf != null) {
                        var s = ""
                        val startTime = System.currentTimeMillis()
                        while (buf.ready() && s.length < (1024 * 16) && ((System.currentTimeMillis() - startTime) < 300)) {
                            s += buf.read().toChar()
                        }
                        if (s.isNotEmpty()) {
                            channelNetworkIn.send(s)
                        }
                    } else {
                        Timber.e("buf == null")
                    }

                } catch (e: IOException) {
                    Timber.e("Ошибка в приемном потоке ${e.message}")
                    withContext(Dispatchers.Main)
                    {
                        btIsConnected = false
                        mSocket?.close()
                    }
                    break  //При отключении подключения
                }
            }
            btStatus = BTstatus.DISCONNECT
        }

    }

    fun sendMessage(byteArray: ByteArray) {
    try {
        mSocket?.outputStream?.write(byteArray)
    } catch (i: IOException) {

    }
}

}

