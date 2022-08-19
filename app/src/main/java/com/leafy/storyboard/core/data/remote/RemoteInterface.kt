package com.leafy.storyboard.core.data.remote

import androidx.paging.PagingData
import com.leafy.storyboard.core.data.local.entity.StoryEntity
import com.leafy.storyboard.core.data.remote.network.ApiResponse
import com.leafy.storyboard.core.data.remote.response.login.LoginResponse
import com.leafy.storyboard.core.data.remote.response.story.StoryResponse
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RemoteInterface {
    suspend fun postRegister(
        name: String,
        email: String,
        password: String
    ): Flow<ApiResponse<String>>

    suspend fun postLogin(email: String, password: String): Flow<ApiResponse<LoginResponse>>
    suspend fun postNewStory(
        token: String,
        imgFile: File,
        description: String,
        lat: Double,
        lon: Double
    ): Flow<ApiResponse<String>>

    fun getStoryList(token: String): Flow<PagingData<StoryEntity>>
    suspend fun getStoryWithLocation(token: String): Flow<ApiResponse<List<StoryResponse>>>
}