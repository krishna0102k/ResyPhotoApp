package com.example.resyphotoapp.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.HttpURLConnection
import java.net.URL

object ImageDownloader {

    fun download(urlString: String): Bitmap {
        val connection = URL(urlString).openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.instanceFollowRedirects = true
            connection.doInput = true

            connection.connect()

            val responseCode = connection.responseCode

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw IllegalStateException(
                    "Image request failed with HTTP $responseCode"
                )
            }

            connection.inputStream.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
                    ?: throw IllegalStateException(
                        "Unable to decode image response"
                    )
            }
        } finally {
            connection.disconnect()
        }
    }

    private const val CONNECT_TIMEOUT_MS = 10_000
    private const val READ_TIMEOUT_MS = 10_000
}
