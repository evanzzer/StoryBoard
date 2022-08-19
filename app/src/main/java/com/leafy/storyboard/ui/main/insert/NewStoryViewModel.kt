package com.leafy.storyboard.ui.main.insert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.leafy.storyboard.core.domain.usecase.DataUseCase
import java.io.File

class NewStoryViewModel(private val useCase: DataUseCase) : ViewModel() {
    lateinit var currentPhotoPath: String
    var file: File? = null

    fun postNewStory(token: String, file: File, description: String, lat: Double, lon: Double) =
        useCase.postNewStory(token, file, description, lat, lon).asLiveData()
}