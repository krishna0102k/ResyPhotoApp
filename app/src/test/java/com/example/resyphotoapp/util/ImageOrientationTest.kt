package com.example.resyphotoapp.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ImageOrientationTest {

    @Test
    fun widerThanTall_returnsLandscape() {
        val result = determineImageOrientation(
            width = 1920,
            height = 1080
        )

        assertEquals(
            ImageOrientation.LANDSCAPE,
            result
        )
    }

    @Test
    fun tallerThanWide_returnsPortrait() {
        val result = determineImageOrientation(
            width = 1080,
            height = 1920
        )

        assertEquals(
            ImageOrientation.PORTRAIT,
            result
        )
    }

    @Test
    fun equalWidthAndHeight_returnsPortrait() {
        val result = determineImageOrientation(
            width = 1000,
            height = 1000
        )

        assertEquals(
            ImageOrientation.PORTRAIT,
            result
        )
    }
}
