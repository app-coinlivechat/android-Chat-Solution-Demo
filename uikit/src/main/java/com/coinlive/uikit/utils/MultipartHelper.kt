package com.coinlive.uikit.utils

import android.content.Context
import android.graphics.Bitmap
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*

object MultipartHelper {

    fun buildBitmapBodyPart(fileName: String, bitmap: Bitmap, context: Context): MultipartBody.Part {
        val leftImageFile = convertBitmapToFile(fileName, bitmap, context)
        val reqFile = leftImageFile.asRequestBody()
        return MultipartBody.Part.createFormData("file", leftImageFile.name, reqFile)
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap, context: Context): File {
        //create a file to write bitmap data
        val file = File(context.cacheDir, fileName)
        file.createNewFile()


        var bmpWidth = bitmap.width
        var bmpHeight = bitmap.height
        if (bmpWidth > 1440) {
            // 원하는 너비보다 클 경우의 설정
            val scale = 1440 / (bmpWidth / 100)
            bmpWidth = (bmpWidth * scale) /100
            bmpHeight = (bmpHeight * scale) /100
        } else if (bmpHeight > 1440) {
            // 원하는 높이보다 클 경우의 설정
            val scale = 1440 / (bmpHeight / 100)
            bmpWidth = (bmpWidth * scale) /100
            bmpHeight = (bmpHeight * scale) /100
        }

        val resizeBitmap = Bitmap.createScaledBitmap(bitmap, bmpWidth, bmpHeight, false)

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 70 /*ignored for PNG*/, bos)

        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }
}