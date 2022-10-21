package com.coinlive.chat.firebase.service

import androidx.room.Database
import androidx.room.RoomDatabase
import com.coinlive.chat.firebase.dao.ChatDao
import com.coinlive.chat.firebase.model.Chat

@Database(entities = [Chat::class], version = 1)
abstract class ChatDatabase : RoomDatabase(){
    abstract fun chatDao():ChatDao
}