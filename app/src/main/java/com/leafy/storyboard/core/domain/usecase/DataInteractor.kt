package com.leafy.storyboard.core.domain.usecase

import com.leafy.storyboard.core.data.DataSource
import com.leafy.storyboard.core.data.utils.Status
import com.leafy.storyboard.core.domain.model.Story
import kotlinx.coroutines.flow.Flow
import java.io.File

class DataInteractor(private val dataRepository: DataSource) : DataUseCase {
    override fun postRegister(name: String, email: String, password: String) =
        dataRepository.postRegister(name, email, password)

    override fun postLogin(email: String, password: String) =
        dataRepository.postLogin(email, password)

    override fun postNewStory(token: String, imgFile: File, description: String, lat: Double, lon: Double) =
        dataRepository.postNewStory(token, imgFile, description, lat, lon)

    override fun getStoryList(token: String) = dataRepository.getStoryList(token)

    override fun getStoryWithLocation(token: String): Flow<Status<List<Story>>> =
        dataRepository.getStoryWithLocation(token)
}