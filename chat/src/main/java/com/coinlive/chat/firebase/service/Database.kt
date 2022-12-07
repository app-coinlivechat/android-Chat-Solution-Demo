package com.coinlive.chat.firebase.service

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.coinlive.chat.firebase.dao.ChatDao
import com.coinlive.chat.firebase.model.Asset
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.Emoji
import com.google.firebase.Timestamp
import com.google.gson.Gson

@Database(entities = [Chat::class], version = 1,exportSchema = false)
@TypeConverters(Converters::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}

class Converters {
    @TypeConverter
    fun fromStringList(value: ArrayList<String>?): String = Gson().toJson(value)

    @TypeConverter
    fun toStringList(value: String): ArrayList<String>? = Gson().fromJson(value, ArrayList<String>()::class.java)

    @TypeConverter
    fun fromEmojiMap(value: HashMap<String, Emoji>?): String = Gson().toJson(value)

    @TypeConverter
    fun toEmojiMap(value: String): HashMap<String, Emoji>? =
        Gson().fromJson(value, HashMap<String, Emoji>()::class.java)

    @TypeConverter
    fun fromAsset(value: Asset?): String = Gson().toJson(value)

    @TypeConverter
    fun toAsset(value: String): Asset? = Gson().fromJson(value, Asset::class.java)


    @TypeConverter
    fun fromTimeStamp(value: Timestamp?): String = Gson().toJson(value)

    @TypeConverter
    fun toTimeStamp(value: String): Timestamp? = Gson().fromJson(value, Timestamp::class.java)

}