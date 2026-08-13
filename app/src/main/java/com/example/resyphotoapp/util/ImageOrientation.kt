package com.example.resyphotoapp.util

enum class ImageOrientation {
    PORTRAIT,
    LANDSCAPE
}

fun determineImageOrientation(
    width: Int,
    height: Int
): ImageOrientation {
    return if (width > height) {
        ImageOrientation.LANDSCAPE
    } else {
        ImageOrientation.PORTRAIT
    }
}
