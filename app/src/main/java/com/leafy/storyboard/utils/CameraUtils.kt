package com.leafy.storyboard.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

object CameraUtils {
    fun createTempFiles(ctx: Context): File {
        val storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timestamp =
            SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(System.currentTimeMillis())
        return File.createTempFile(timestamp, ".jpg", storageDir)
    }

    fun uriToFile(ctx: Context, image: Uri): File {
        val contentResolver: ContentResolver = ctx.contentResolver
        val myFile = createTempFiles(ctx)

        val inputStream = contentResolver.openInputStream(image) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)

        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)

        outputStream.close()
        inputStream.close()

        return myFile
    }

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1_000_000) // 1MB

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }
}