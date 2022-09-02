package com.shaluambasta.bluechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.shaluambasta.bluechat.BluetoothDevicesActivity
import com.shaluambasta.bluechat.R
import com.shaluambasta.bluechat.model.BluetoothDevicesModel

class BluetoothDevicesAdapter(

    private val context: Context,
    private val dataset: MutableSet<BluetoothDevicesModel>

) : RecyclerView.Adapter<BluetoothDevicesAdapter.BluetoothDevicesViewHolder>() {

    inner class BluetoothDevicesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val devicesRecyclerCard: MaterialCardView = view.findViewById(R.id.devices_recycler_card)
        val textDeviceName: MaterialTextView = view.findViewById(R.id.text_device_name)
        val textDeviceAddress: MaterialTextView = view.findViewById(R.id.text_device_address)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDevicesViewHolder {
        val adapterLayout = LayoutInflater.from(context)
            .inflate(R.layout.devices_recycler_single_item, parent, false)

        return BluetoothDevicesViewHolder(adapterLayout)
    }


    override fun onBindViewHolder(holder: BluetoothDevicesViewHolder, position: Int) {

        val item = dataset.elementAt(position)
        val objectBluetoothDevicesActivity = BluetoothDevicesActivity()

        holder.devicesRecyclerCard.setOnClickListener {
            objectBluetoothDevicesActivity.onListItemClick(context, item)
        }

        holder.textDeviceName.text = item.deviceName
        holder.textDeviceAddress.text = item.deviceAddress

    }

    override fun getItemCount(): Int {
        return dataset.size
    }


}