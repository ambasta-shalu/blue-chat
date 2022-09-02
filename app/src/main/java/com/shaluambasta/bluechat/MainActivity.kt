package com.shaluambasta.bluechat

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView


class MainActivity : AppCompatActivity() {


    private lateinit var toolbarMain: MaterialToolbar
    private lateinit var textStarter: MaterialTextView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var fabChat: FloatingActionButton

    private lateinit var bluetoothAdapter: BluetoothAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbarMain = findViewById(R.id.toolbar_main)
        textStarter = findViewById(R.id.text_starter)
        chatRecyclerView = findViewById(R.id.chat_recycler_view)
        fabChat = findViewById(R.id.fab_chat)

        setSupportActionBar(toolbarMain)


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        initBluetooth()
        enableBluetooth()

        fabChat.setOnClickListener {

            enableBluetooth()
            val intent = Intent(this, BluetoothDevicesActivity::class.java)
            startActivity(intent)

        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_delete -> {
                Toast.makeText(this, "Action delete implemented soon", Toast.LENGTH_SHORT).show()
                return true
            }

            R.id.action_settings -> {
                Toast.makeText(this, "Action settings implemented soon", Toast.LENGTH_SHORT).show()
                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }

        }
    }

    private fun initBluetooth() {

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "This device doesn't support bluetooth :(", Toast.LENGTH_SHORT)
                .show()
            finish()
        }

    }

    private fun enableBluetooth() {

        if (!bluetoothAdapter.isEnabled) {

            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_SCAN
            )
            ActivityCompat.requestPermissions(this, permissions, 0)

        }
        if (bluetoothAdapter.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            val discoveryIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
            startActivity(discoveryIntent)
        }
    }

}