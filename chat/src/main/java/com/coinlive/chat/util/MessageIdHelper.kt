package com.coinlive.chat.util

import java.security.SecureRandom
import java.util.*

object MessageIdHelper {
    private const val AUTO_ID_LENGTH = 12

    private const val AUTO_ID_ALPHABET = "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz"

    private val rand: Random = SecureRandom()
    fun generateMessageId(): String {
        val builder = StringBuilder()
        val maxRandom = AUTO_ID_ALPHABET.length
        for (i in 0 until AUTO_ID_LENGTH) {
            builder.append(AUTO_ID_ALPHABET[rand.nextInt(maxRandom)])
        }
        return builder.toString()
    }

}