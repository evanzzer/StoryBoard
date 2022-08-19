package com.leafy.storyboard.di

import com.leafy.storyboard.core.domain.usecase.DataInteractor
import com.leafy.storyboard.core.domain.usecase.DataUseCase
import com.leafy.storyboard.ui.login.LoginViewModel
import com.leafy.storyboard.ui.main.MainViewModel
import com.leafy.storyboard.ui.main.insert.NewStoryViewModel
import com.leafy.storyboard.ui.main.maps.MapsViewModel
import com.leafy.storyboard.ui.register.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {
    val useCaseModule = module {
        factory<DataUseCase> { DataInteractor(get()) }
    }

    val viewModelModule = module {
        viewModel { LoginViewModel(get()) }
        viewModel { RegisterViewModel(get()) }
        viewModel { MainViewModel(get()) }
        viewModel { NewStoryViewModel(get()) }
        viewModel { MapsViewModel(get()) }
    }
}