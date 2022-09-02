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

class SentMessageAdapter(

    private val context: Context,
    private val dataset: MutableList<MessageModel>

) : RecyclerView.Adapter<SentMessageAdapter.SentMessageViewHolder>() {


    inner class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val cardSentMessage: MaterialCardView = view.findViewById(R.id.card_sent_message)
        val textSentMessage: TextView = view.findViewById(R.id.text_sent_message)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentMessageViewHolder {

        val adapterLayout = LayoutInflater.from(context)
            .inflate(R.layout.sent_message_recycler_single_item, parent, false)
        return SentMessageViewHolder(adapterLayout)
    }


    override fun onBindViewHolder(holder: SentMessageViewHolder, position: Int) {

        val item = dataset[position]
        holder.textSentMessage.text = item.message
    }


    override fun getItemCount(): Int {
        return dataset.size
    }


}