package com.coinlive.chat.firebase.model

import com.google.gson.annotations.SerializedName

data class Asset(
    @SerializedName("selectedEmoji") val emoji:String,
    val exchange:String,
    val amount:Int,
    val priceWon:Int,
    val priceDol:Int,
    val nickname:Int,
    val coinId:String,
    val symbol:String,
    val iconUrl:String
)
