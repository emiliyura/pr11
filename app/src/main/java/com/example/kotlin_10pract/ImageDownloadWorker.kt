package com.example.kotlin_10pract

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.work.Data
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageDownloadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val url = inputData.getString("url") ?: return Result.failure()

        val imageBitmap = loadImageFromNetwork(url)

        if (imageBitmap != null) {
            saveImageToDisk(applicationContext, imageBitmap)
            return Result.success(Data.Builder().putString("filePath", getFilePath(applicationContext)).build())
        } else {
            return Result.failure()
        }
    }

    private fun loadImageFromNetwork(url: String): Bitmap? {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        return try {
            val response = client.newCall(request).execute()
            val inputStream: InputStream = response.body!!.byteStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveImageToDisk(context: Context, bitmap: Bitmap) {
        val file = File(context.getExternalFilesDir(null), "downloaded_image.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    private fun getFilePath(context: Context): String {
        return File(context.getExternalFilesDir(null), "downloaded_image.png").absolutePath
    }
}