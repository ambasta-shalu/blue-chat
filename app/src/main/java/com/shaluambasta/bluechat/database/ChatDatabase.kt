package com.shaluambasta.bluechat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shaluambasta.bluechat.dao.ChatDao
import com.shaluambasta.bluechat.entity.ChatEntity

@Database(entities = [ChatEntity::class], version = 1)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}