package com.shaluambasta.bluechat.utils

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class BluetoothChatService(
    private var context: Context,
    private var handler: Handler
) {

    companion object {

        val uuid: UUID = UUID.fromString(UUID_KEY)

        private const val TAG = "BluetoothChatService"
        private const val APP_NAME = "Blue Chat"

        const val STATE_NONE: Int = 0
        const val STATE_LISTEN: Int = 1
        const val STATE_CONNECTING: Int = 2
        const val STATE_CONNECTED: Int = 3

    }


    private var bluetoothAdapter: BluetoothAdapter? = null
    private var acceptThread: AcceptDevicesThread? = null
    private var connectThread: ConnectDevicesThread? = null
    private var connectedThread: ConnectedThread? = null
    private var mState: Int? = null


    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mState = STATE_NONE
    }


    @Synchronized
    private fun setState(newState: Int) {
        mState = newState
        handler.obtainMessage(MESSAGE_STATE_CHANGED, mState!!, -1).sendToTarget()
    }


    @Synchronized
    fun start() {

        Log.d(TAG, "start")

        if (connectThread != null) {
            connectThread!!.cancel()
            connectThread = null
        }


        if (acceptThread == null) {
            acceptThread = AcceptDevicesThread()
            acceptThread!!.start()
        }

        if (connectedThread != null) {
            connectedThread!!.cancel()
            connectedThread = null
        }

        setState(STATE_LISTEN)

    }


    @Synchronized
    fun stop() {

        Log.d(TAG, "stop")

        if (connectThread != null) {
            connectThread!!.cancel()
            connectThread = null
        }


        if (acceptThread != null) {
            acceptThread!!.cancel()
            acceptThread = null
        }


        if (connectedThread != null) {
            connectedThread!!.cancel()
            connectedThread = null
        }


        setState(STATE_NONE)

    }

    @Synchronized
    fun connect(bluetoothDevice: BluetoothDevice) {

        if (mState == STATE_CONNECTING) {
            connectThread?.cancel()
            connectThread = null
        }

        if (connectedThread != null) {
            connectedThread!!.cancel()
            connectedThread = null
        }

        connectThread = ConnectDevicesThread(bluetoothDevice)
        connectThread!!.start()

        setState(STATE_CONNECTING)

    }


    @Synchronized
    fun connected(bluetoothSocket: BluetoothSocket, bluetoothDevice: BluetoothDevice) {

        Log.d(TAG, "connect to: $bluetoothDevice")

        if (connectThread != null) {
            connectThread!!.cancel()
            connectThread = null
        }

        if (connectedThread != null) {
            connectedThread!!.cancel()
            connectedThread = null
        }

        connectedThread = ConnectedThread(bluetoothSocket)
        connectedThread!!.start()


        setState(STATE_CONNECTED)
    }


    fun write(out: ByteArray?) {

        var thread: ConnectedThread
        synchronized(this) {

            if (mState != STATE_CONNECTED) {
                return
            }
            thread = connectedThread!!
        }
        thread.write(out!!)
    }


    private fun connectionFailed() {

        val msg: Message = handler.obtainMessage(MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(TOAST_KEY, "Unable to connect device :(")
        msg.data = bundle
        handler.sendMessage(msg)

        setState(STATE_NONE)

        start()
    }


    private fun connectionLost() {

        val msg: Message = handler.obtainMessage(MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(TOAST_KEY, "Device connection lost :(")
        msg.data = bundle
        handler.sendMessage(msg)

        setState(STATE_NONE)

        start()
    }


    private inner class ConnectDevicesThread(
        private val bluetoothDevice: BluetoothDevice
    ) : Thread() {


        private lateinit var bluetoothSocket: BluetoothSocket

        override fun run() {

            try {

                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val permissions = arrayOf(
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                    ActivityCompat.requestPermissions(context as Activity, permissions, 0)
                    return
                } else {

                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)

                    setState(STATE_CONNECTING)

                    try {

                        bluetoothAdapter!!.cancelDiscovery()
                        bluetoothSocket.connect()

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                        cancel()
                        connectionFailed()
                        return
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }


            synchronized(this) {
                connectThread = null
            }

            connected(bluetoothSocket, bluetoothDevice)

        }

        fun cancel() {
            try {
                bluetoothSocket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }


    private inner class AcceptDevicesThread : Thread() {

        private lateinit var bluetoothServerSocket: BluetoothServerSocket
        private lateinit var bluetoothSocket: BluetoothSocket


        override fun run() {

            try {

                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val permissions = arrayOf(
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                    ActivityCompat.requestPermissions(context as Activity, permissions, 0)
                    return

                } else {
                    bluetoothServerSocket =
                        bluetoothAdapter!!.listenUsingRfcommWithServiceRecord(APP_NAME, uuid)

                    setState(STATE_LISTEN)

                    try {
                        bluetoothSocket = bluetoothServerSocket.accept()

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                        cancel()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }


            if (bluetoothSocket != null) {
                when (mState) {
                    STATE_LISTEN -> {
                        Log.d(TAG, "STATE LISTENING")
                    }
                    STATE_CONNECTING -> {
//                        connect(bluetoothSocket.remoteDevice)
                        connected(bluetoothSocket, bluetoothSocket.remoteDevice)
                    }
                    STATE_NONE -> {
                        Log.d(TAG, "STATE NONE")

                    }
                    STATE_CONNECTED -> {
                        try {
                            bluetoothSocket.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }


        }

        fun cancel() {
            try {
                bluetoothServerSocket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private inner class ConnectedThread(
        private val bluetoothSocket: BluetoothSocket
    ) : Thread() {

        private lateinit var inputStream: InputStream
        private lateinit var outputStream: OutputStream


        override fun run() {

            try {

                inputStream = bluetoothSocket.inputStream
                outputStream = bluetoothSocket.outputStream

                setState(STATE_CONNECTED)

                val buffer = ByteArray(1024)
                var bytes: Int?

                try {
                    while (mState == STATE_CONNECTED) {

                        bytes = inputStream.read(buffer)
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget()

                    }

                } catch (e: Exception) {
                    connectionLost()
                    e.printStackTrace()
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                    cancel()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                cancel()
            }

        }


        fun write(buffer: ByteArray) {

            try {

                outputStream.write(buffer)
                handler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget()

            } catch (e: Exception) {
                connectionFailed()
                e.printStackTrace()
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                cancel()
            }

        }


        fun cancel() {
            try {
                bluetoothSocket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

}

