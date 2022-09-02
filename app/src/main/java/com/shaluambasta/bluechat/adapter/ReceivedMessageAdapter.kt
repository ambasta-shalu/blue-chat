package com.shaluambasta.bluechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.shaluambasta.bluechat.R
import com.shaluambasta.bluechat.model.MessageModel

class ReceivedMessageAdapter(

    private val context: Context,
    private val dataset: MutableList<MessageModel>

) : RecyclerView.Adapter<ReceivedMessageAdapter.ReceivedMessageViewHolder>() {


    inner class ReceivedMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val cardReceivedMessage: MaterialCardView = view.findViewById(R.id.card_received_message)
        val textReceivedMessage: TextView = view.findViewById(R.id.text_received_message)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceivedMessageViewHolder {
        val adapterLayout = LayoutInflater.from(context)
            .inflate(R.layout.received_message_recycler_single_item, parent, false)
        return ReceivedMessageViewHolder(adapterLayout)
    }


    override fun onBindViewHolder(holder: ReceivedMessageViewHolder, position: Int) {

        val item = dataset[position]
        holder.textReceivedMessage.text = item.message

    }


    override fun getItemCount(): Int {
        return dataset.size
    }


}