package com.shaluambasta.bluechat

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.shaluambasta.bluechat.adapter.ReceivedMessageAdapter
import com.shaluambasta.bluechat.adapter.SentMessageAdapter
import com.shaluambasta.bluechat.model.MessageModel
import com.shaluambasta.bluechat.utils.*

class ChatToDevicesActivity : AppCompatActivity() {


    private lateinit var toolbarChat: MaterialToolbar
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var rlChatBox: RelativeLayout
    private lateinit var etMessage: EditText
    private lateinit var fabSend: FloatingActionButton


    private lateinit var handler: Handler
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothChatService: BluetoothChatService


    private lateinit var deviceName: String
    private lateinit var deviceAddress: String
    private val TAG: String = "ChatToDevicesActivity"


    private var sentMessagesDataset: MutableList<MessageModel> = mutableListOf()
    private var receivedMessageDataset: MutableList<MessageModel> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_to_devices)


        toolbarChat = findViewById(R.id.toolbar_chat)
        messageRecyclerView = findViewById(R.id.message_recycler_view)
        rlChatBox = findViewById(R.id.rl_chat_box)
        etMessage = findViewById(R.id.et_message)
        fabSend = findViewById(R.id.fab_send)


        setSupportActionBar(toolbarChat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (intent != null) {

            deviceName = intent.getStringExtra(DEVICE_NAME_KEY).toString()
            deviceAddress = intent.getStringExtra(DEVICE_ADDRESS_KEY).toString()

            Log.d(TAG, deviceName)
            Log.d(TAG, deviceAddress)

        } else {
            Toast.makeText(this, "Bad Gateway 🤨", Toast.LENGTH_SHORT).show()
            finish()
        }

        toolbarChat.title = deviceName
        toolbarChat.subtitle = deviceAddress

        messageRecyclerView.setHasFixedSize(true)


        handler = Handler(Handler.Callback {

            when (it.what) {

                MESSAGE_STATE_CHANGED -> {

                    when (it.arg1) {

                        BluetoothChatService.STATE_NONE -> setState("Not Connected :(")
                        BluetoothChatService.STATE_LISTEN -> setState("Listening...")
                        BluetoothChatService.STATE_CONNECTING -> setState("Connecting...")
                        BluetoothChatService.STATE_CONNECTED -> setState("Connected :)")

                    }

                }

                MESSAGE_READ -> {

                    val buffer: ByteArray = it.obj as ByteArray
                    val inputBuffer: String = String(buffer, 0, it.arg1)

                    receivedMessageDataset.add(MessageModel(inputBuffer))
                    messageRecyclerView.adapter =
                        ReceivedMessageAdapter(this, receivedMessageDataset)
                    messageRecyclerView.adapter?.notifyDataSetChanged()

                }

                MESSAGE_WRITE -> {

                    val buffer: ByteArray = it.obj as ByteArray
                    val outputBuffer: String = String(buffer)

                    sentMessagesDataset.add(MessageModel(outputBuffer))
                    messageRecyclerView.adapter = SentMessageAdapter(this, sentMessagesDataset)
                    messageRecyclerView.adapter?.notifyDataSetChanged()


                }
                MESSAGE_TOAST -> {
                    Toast.makeText(this, it.data.getString(TOAST_KEY), Toast.LENGTH_SHORT).show()
                }

            }
            return@Callback true
        })

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
        bluetoothChatService = BluetoothChatService(this, handler)


        bluetoothChatService.connect(bluetoothDevice)


        fabSend.setOnClickListener {

            val msg = etMessage.text.toString()
            if (msg.isNotEmpty() && msg.isNotBlank()) {
                sendMessage(msg)
            } else {
                Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun onDestroy() {
        bluetoothChatService.stop()
        super.onDestroy()
    }


    private fun setState(subTitle: String) {
        toolbarChat.subtitle = subTitle
    }


    private fun sendMessage(msg: String) {

        val send: ByteArray = msg.toByteArray()
        bluetoothChatService.write(send)

        etMessage.setText("")

    }

}