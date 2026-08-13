package com.example.resyphotoapp.util

object ImageUrlBuilder {

    fun build(
        id: Int,
        width: Int,
        height: Int
    ): String {
        return "https://picsum.photos/$width/$height?image=$id"
    }
}
