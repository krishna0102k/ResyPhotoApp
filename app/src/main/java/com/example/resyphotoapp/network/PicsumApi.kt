package com.example.resyphotoapp.network

import com.example.resyphotoapp.data.model.Photo
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class PicsumApi {

    fun getPhotos(): List<Photo> {
        val connection = URL(LIST_URL).openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS

            val responseCode = connection.responseCode

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw IllegalStateException(
                    "Photo list request failed with HTTP $responseCode"
                )
            }

            val response = connection.inputStream
                .bufferedReader()
                .use { reader ->
                    reader.readText()
                }

            parsePhotos(response)
        } finally {
            connection.disconnect()
        }
    }

    internal fun parsePhotos(json: String): List<Photo> {
        val array = JSONArray(json)
        val photos = mutableListOf<Photo>()

        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)

            photos.add(
                Photo(
                    id = item.getInt("id"),
                    author = item.getString("author"),
                    width = item.getInt("width"),
                    height = item.getInt("height"),
                    filename = item.getString("filename")
                )
            )
        }

        return photos
    }

    private companion object {
        const val LIST_URL = "https://picsum.photos/list"
        const val CONNECT_TIMEOUT_MS = 10_000
        const val READ_TIMEOUT_MS = 10_000
    }
}
