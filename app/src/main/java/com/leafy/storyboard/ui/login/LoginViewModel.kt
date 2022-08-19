package com.leafy.storyboard.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.leafy.storyboard.core.domain.usecase.DataUseCase

class LoginViewModel(private val useCase: DataUseCase) : ViewModel() {
    fun postLogin(email: String, password: String) =
        useCase.postLogin(email, password).asLiveData()
}