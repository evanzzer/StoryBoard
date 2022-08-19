package com.leafy.storyboard.core.domain.usecase

import androidx.paging.PagingData
import com.leafy.storyboard.core.data.utils.Status
import com.leafy.storyboard.core.domain.model.Login
import com.leafy.storyboard.core.domain.model.Story
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataUseCase {
    fun postRegister(name: String, email: String, password: String): Flow<Status<String>>
    fun postLogin(email: String, password: String): Flow<Status<Login>>
    fun postNewStory(token: String, imgFile: File, description: String, lat: Double, lon: Double): Flow<Status<String>>
    fun getStoryList(token: String): Flow<PagingData<Story>>
    fun getStoryWithLocation(token: String): Flow<Status<List<Story>>>
}