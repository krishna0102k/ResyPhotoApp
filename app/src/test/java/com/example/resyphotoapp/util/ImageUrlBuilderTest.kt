package com.example.resyphotoapp.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ImageUrlBuilderTest {

    @Test
    fun build_returnsExpectedPicsumUrl() {
        val result = ImageUrlBuilder.build(
            id = 42,
            width = 1920,
            height = 1080
        )

        assertEquals(
            "https://picsum.photos/1920/1080?image=42",
            result
        )
    }
}
