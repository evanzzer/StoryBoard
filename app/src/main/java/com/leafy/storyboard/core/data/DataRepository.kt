package com.leafy.storyboard.core.data

import androidx.paging.map
import com.leafy.storyboard.core.data.remote.RemoteInterface
import com.leafy.storyboard.core.data.remote.response.login.LoginResponse
import com.leafy.storyboard.core.data.remote.response.story.StoryResponse
import com.leafy.storyboard.core.data.utils.NetworkResource
import com.leafy.storyboard.core.domain.model.Login
import com.leafy.storyboard.core.domain.model.Story
import com.leafy.storyboard.core.utils.ObjectMapper.toLogin
import com.leafy.storyboard.core.utils.ObjectMapper.toStory
import kotlinx.coroutines.flow.map
import java.io.File

class DataRepository(private val remoteDataSource: RemoteInterface) : DataSource {
    override fun postRegister(name: String, email: String, password: String) =
        object : NetworkResource<String, String>() {
            override fun convertResponse(data: String) = data

            override suspend fun createCall() = remoteDataSource.postRegister(name, email, password)
        }.asFlow()

    override fun postLogin(email: String, password: String) =
        object : NetworkResource<LoginResponse, Login>() {
            override fun convertResponse(data: LoginResponse) = data.toLogin()

            override suspend fun createCall() = remoteDataSource.postLogin(email, password)
        }.asFlow()

    override fun postNewStory(token: String, imgFile: File, description: String, lat: Double, lon: Double) =
        object : NetworkResource<String, String>() {
            override fun convertResponse(data: String) = data

            override suspend fun createCall() =
                remoteDataSource.postNewStory(token, imgFile, description, lat, lon)
        }.asFlow()

    override fun getStoryList(token: String) =
        remoteDataSource.getStoryList(token)
            .map { pagingData ->
                pagingData.map { it.toStory() }
            }


    override fun getStoryWithLocation(token: String) =
        object : NetworkResource<List<StoryResponse>, List<Story>>() {
            override fun convertResponse(data: List<StoryResponse>) = data.map { it.toStory() }

            override suspend fun createCall() = remoteDataSource.getStoryWithLocation(token)
        }.asFlow()
}