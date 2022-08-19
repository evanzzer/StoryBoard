package com.leafy.storyboard.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.leafy.storyboard.core.domain.usecase.DataUseCase

class RegisterViewModel(private val useCase: DataUseCase) : ViewModel() {
    fun postRegister(name: String, email: String, password: String) =
        useCase.postRegister(name, email, password).asLiveData()
}