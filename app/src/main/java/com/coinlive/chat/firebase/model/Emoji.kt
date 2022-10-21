package com.coinlive.chat.firebase.model

import com.google.gson.annotations.SerializedName

data class Emoji(
    @SerializedName("cnt") val count:Int,
    @SerializedName("mid") val mIds:List<String>
)


/*
static const String emojiGood = ':+1:';
  static const String emojiHeart = ':heart:';
  static const String emojiClap = ':clap:';
  static const String emojiRocket = ':rocket:';
  static const String emojiCry = ':cry:';
  static const String emojiAstonished = ':astonished:';
 */
enum class EmojiType(value:String) {
    GOOD(":+1:"),
    HEART(":heart:"),
    CLAP(":clap:"),
    ROCKET(":rocket:"),
    CRY(":cry:"),
    IASTONISHED(":astonished:");
}
