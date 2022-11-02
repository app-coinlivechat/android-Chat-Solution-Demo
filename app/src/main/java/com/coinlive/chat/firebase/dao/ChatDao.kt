package com.coinlive.chat.firebase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.coinlive.chat.firebase.model.Chat

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat WHERE messageId = :messageId")
    fun getMessage(messageId: String): Chat?

    @Query("SELECT * FROM chat")
    fun getAllMessage(): ArrayList<Chat>?

    @Query("DELETE FROM chat WHERE messageId = :messageId")
    fun deleteMessage(messageId: String)

    @Insert
    fun insertMessage(chat: Chat)

    @Query("DELETE FROM chat WHERE insertTime <= :standardTime")
    fun deleteOldMessage(standardTime: Long)
}