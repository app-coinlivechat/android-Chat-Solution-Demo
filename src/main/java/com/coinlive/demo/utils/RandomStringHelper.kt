package com.coinlive.demo.utils

import java.security.SecureRandom
import java.util.*

object RandomStringHelper {
    private const val AUTO_ID_LENGTH = 9

    private const val AUTO_ID_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    private val rand: Random = SecureRandom()
    fun generateString(): String {
        val builder = StringBuilder()
        val maxRandom = AUTO_ID_ALPHABET.length
        for (i in 0 until AUTO_ID_LENGTH) {
            builder.append(AUTO_ID_ALPHABET[rand.nextInt(maxRandom)])
        }
        return builder.toString()
    }

}