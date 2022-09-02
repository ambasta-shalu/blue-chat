package com.shaluambasta.bluechat.dao

import androidx.room.*
import com.shaluambasta.bluechat.entity.ChatEntity

@Dao
interface ChatDao {

    @Query("SELECT * FROM chat_entity")
    fun getAll(): List<ChatEntity>


    @Query("SELECT * FROM chat_entity WHERE device_name == (:name)")
    fun getByName(name: String): List<ChatEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(chatEntity: ChatEntity)


    @Delete
    fun delete(chatEntity: ChatEntity)

}