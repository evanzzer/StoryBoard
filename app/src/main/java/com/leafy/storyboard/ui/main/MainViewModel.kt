package com.leafy.storyboard.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.leafy.storyboard.core.domain.usecase.DataUseCase

class MainViewModel(private val useCase: DataUseCase) : ViewModel() {
    fun getStoryList(token: String) = useCase.getStoryList(token).asLiveData().cachedIn(viewModelScope)
}