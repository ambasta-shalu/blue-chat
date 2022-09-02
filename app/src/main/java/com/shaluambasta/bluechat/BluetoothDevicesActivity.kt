package com.shaluambasta.bluechat

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.shaluambasta.bluechat.adapter.BluetoothDevicesAdapter
import com.shaluambasta.bluechat.model.BluetoothDevicesModel
import com.shaluambasta.bluechat.utils.DEVICE_ADDRESS_KEY
import com.shaluambasta.bluechat.utils.DEVICE_NAME_KEY

class BluetoothDevicesActivity : AppCompatActivity() {


    private lateinit var bluetoothAdapter: BluetoothAdapter


    private lateinit var toolbarDevices: MaterialToolbar
    private lateinit var circularProgress: CircularProgressIndicator
    private lateinit var availableDevicesRecyclerView: RecyclerView
    private lateinit var pairedDevicesRecyclerView: RecyclerView
    private lateinit var searchDevicesListView: ListView


    private var availableDevicesDataset: MutableSet<BluetoothDevicesModel> = mutableSetOf()
    private var pairedDevicesDataset: MutableSet<BluetoothDevicesModel> = mutableSetOf()
    private var searchDataset: MutableList<String> = mutableListOf()
    private lateinit var searchAdapter: ArrayAdapter<String>

    private var deviceFlag: Boolean = true
    private val TAG: String = "BluetoothDevicesActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_devices)

        toolbarDevices = findViewById(R.id.toolbar_devices)
        circularProgress = findViewById(R.id.circular_progress)
        availableDevicesRecyclerView = findViewById(R.id.available_devices_recycler_view)
        pairedDevicesRecyclerView = findViewById(R.id.paired_devices_recycler_view)
        searchDevicesListView = findViewById(R.id.search_devices_list_view)


        setSupportActionBar(toolbarDevices)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        Toast.makeText(this, "Wait, fetching available devices...", Toast.LENGTH_SHORT).show()
        getAvailableDevices()


        availableDevicesRecyclerView.adapter =
            BluetoothDevicesAdapter(this, availableDevicesDataset)
        availableDevicesRecyclerView.setHasFixedSize(true)
        pairedDevicesRecyclerView.adapter = BluetoothDevicesAdapter(this, pairedDevicesDataset)
        pairedDevicesRecyclerView.setHasFixedSize(true)

        searchAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, searchDataset)
        searchDevicesListView.adapter = searchAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_bluetooth_devices, menu)

        val actionSearchDevices: MenuItem? = menu?.findItem(R.id.action_search_devices)
        val searchView: SearchView = actionSearchDevices?.actionView as SearchView

        searchView.background = Color.TRANSPARENT.toDrawable()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                searchAdapter.filter.filter(newText)
                return false
            }

        })


        val actionPairedDevices: MenuItem? = menu.findItem(R.id.action_paired_devices)
        actionPairedDevices?.setOnMenuItemClickListener {

            if (deviceFlag) {

                getPairedDevices()
                deviceFlag = !deviceFlag
                actionPairedDevices.title = "Available Devices"
                toolbarDevices.title = "Paired Devices"

            } else {

                getAvailableDevices()
                deviceFlag = !deviceFlag
                actionPairedDevices.title = "Paired Devices"
                toolbarDevices.title = "Available Devices"

            }
            return@setOnMenuItemClickListener true
        }

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_scan_nearby -> {

                if (deviceFlag) {

                    circularProgress.visibility = View.VISIBLE
                    getAvailableDevices()
                    Toast.makeText(this, "Fetching devices nearby...", Toast.LENGTH_SHORT).show()

                } else {

                    circularProgress.visibility = View.VISIBLE
                    getPairedDevices()
                    Toast.makeText(this, "Fetching paired devices...", Toast.LENGTH_SHORT).show()

                }
                return true
            }

            android.R.id.home -> {
                finish()
                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }

        }
    }


    private fun getAvailableDevices() {

        availableDevicesDataset.clear()
        searchDataset.clear()


        circularProgress.visibility = View.VISIBLE
        availableDevicesRecyclerView.visibility = View.GONE
        pairedDevicesRecyclerView.visibility = View.GONE
        searchDevicesListView.visibility = View.GONE


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT
            )
            ActivityCompat.requestPermissions(this, permissions, 0)

        }
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }

        bluetoothAdapter.startDiscovery()


        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {

                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                        if (device != null) {

                            if (ActivityCompat.checkSelfPermission(
                                    this@BluetoothDevicesActivity,
                                    Manifest.permission.BLUETOOTH_CONNECT
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                val permissions = arrayOf(
                                    Manifest.permission.BLUETOOTH_CONNECT
                                )
                                ActivityCompat.requestPermissions(
                                    this@BluetoothDevicesActivity,
                                    permissions,
                                    0
                                )
                            }

                            if (device.bondState != BluetoothDevice.BOND_BONDED) {

                                val deviceName = device.name
                                val deviceHardwareAddress = device.address

                                availableDevicesDataset.add(
                                    BluetoothDevicesModel(
                                        deviceName,
                                        deviceHardwareAddress
                                    )
                                )

                                availableDevicesRecyclerView.adapter?.notifyDataSetChanged()
                                searchDataset.add(deviceName)
                            }
                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        circularProgress.visibility = View.GONE
                        availableDevicesRecyclerView.visibility = View.VISIBLE
                    }
                }

            }
        }

        val foundFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(broadcastReceiver, foundFilter)

        val finishedFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(broadcastReceiver, finishedFilter)

    }


    private fun getPairedDevices() {

        pairedDevicesDataset.clear()

        circularProgress.visibility = View.VISIBLE
        availableDevicesRecyclerView.visibility = View.GONE
        pairedDevicesRecyclerView.visibility = View.GONE
        searchDevicesListView.visibility = View.GONE

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }

        val pairedDevices = bluetoothAdapter.bondedDevices

        if (pairedDevices.size > 0) {
            for (device in pairedDevices) {

                val deviceName = device.name
                val deviceAddress = device.address

                pairedDevicesDataset.add(
                    BluetoothDevicesModel(
                        deviceName, deviceAddress
                    )
                )
                pairedDevicesRecyclerView.adapter?.notifyDataSetChanged()
                circularProgress.visibility = View.GONE
                pairedDevicesRecyclerView.visibility = View.VISIBLE

            }
        } else {
            circularProgress.visibility = View.GONE
            pairedDevicesRecyclerView.visibility = View.VISIBLE
            Toast.makeText(this, "No Paired Devices found :(", Toast.LENGTH_SHORT).show()
        }

    }

    fun onListItemClick(context: Context, item: BluetoothDevicesModel) {

        Toast.makeText(context, "Preparing chat to ${item.deviceName}", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "clicked.....")

        val intent = Intent(context, ChatToDevicesActivity::class.java).apply {
            putExtra(DEVICE_NAME_KEY, item.deviceName)
            putExtra(DEVICE_ADDRESS_KEY, item.deviceAddress)
        }
        context.startActivity(intent)
        finish()

    }


}