package com.shaluambasta.bluechat.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "chat_entity")
data class ChatEntity(

    @PrimaryKey @ColumnInfo(name = "device_name") val deviceName: String?,
    @ColumnInfo(name = "device_last_message") val deviceLastMessage: String?

)
