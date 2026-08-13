package com.example.resyphotoapp.data

import com.example.resyphotoapp.data.model.Photo
import com.example.resyphotoapp.network.PicsumApi

class PhotoRepository(
    private val api: PicsumApi = PicsumApi()
) {

    fun getPhotos(): List<Photo> {
        return api.getPhotos()
    }
}
