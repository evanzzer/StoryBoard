package com.leafy.storyboard.ui.main.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.leafy.storyboard.core.domain.usecase.DataUseCase

class MapsViewModel(private val useCase: DataUseCase) : ViewModel() {
    fun getStoryWithLocation(token: String) = useCase.getStoryWithLocation(token).asLiveData()
}