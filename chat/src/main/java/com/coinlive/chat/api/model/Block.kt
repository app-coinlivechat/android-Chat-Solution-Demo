package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

data class BlockList(
    @SerializedName("bmid") val mIds:ArrayList<String>
)
