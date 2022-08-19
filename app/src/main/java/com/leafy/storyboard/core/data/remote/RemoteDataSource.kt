package com.leafy.storyboard.core.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.leafy.storyboard.core.data.local.room.StoryDatabase
import com.leafy.storyboard.core.data.remote.network.ApiResponse
import com.leafy.storyboard.core.data.remote.network.ApiService
import com.leafy.storyboard.core.data.remote.paging.StoryRemoteMediator
import com.leafy.storyboard.core.data.remote.request.LoginRequest
import com.leafy.storyboard.core.data.remote.request.RegisterRequest
import com.leafy.storyboard.core.data.remote.response.story.StoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RemoteDataSource(private val database: StoryDatabase,
                       private val apiService: ApiService) : RemoteInterface {
    override suspend fun postRegister(name: String, email: String, password: String) =
        flow {
            try {
                val response = apiService.postRegister(RegisterRequest(name, email, password))
                emit(
                    if (!response.error)
                        ApiResponse.Success(response.message)
                    else ApiResponse.Error(response.message)
                )
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun postLogin(email: String, password: String) =
        flow {
            try {
                val response = apiService.postLogin(LoginRequest(email, password))
                emit(
                    if (!response.error && response.loginResult != null)
                        ApiResponse.Success(response.loginResult)
                    else ApiResponse.Error(response.message)
                )
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun postNewStory(token: String, imgFile: File, description: String, lat: Double, lon: Double) =
        flow {
            try {
                val parsedDesc = description.toRequestBody("text/plain".toMediaType())
                val imgMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    imgFile.name,
                    imgFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )

                val response = apiService.postNewStory("Bearer $token", imgMultipart, parsedDesc, lat, lon)
                emit(
                    if (!response.error)
                        ApiResponse.Success(response.message)
                    else ApiResponse.Error(response.message)
                )
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalPagingApi::class)
    override fun getStoryList(token: String) =
        Pager(
            config = PagingConfig(initialLoadSize = 5, pageSize = 5),
            remoteMediator = StoryRemoteMediator("Bearer $token", database, apiService),
            pagingSourceFactory = {
                database.storyDao().getAllQuote()
            }
        ).flow


    override suspend fun getStoryWithLocation(token: String): Flow<ApiResponse<List<StoryResponse>>> =
        flow {
            try {
                val response = apiService.getStoryList("Bearer $token", page = 1, size = 8, location = 1)
                emit(
                    if (!response.error) {
                        if (response.listStory.isNullOrEmpty())
                            ApiResponse.Empty
                        else ApiResponse.Success(response.listStory)
                    } else ApiResponse.Error(response.message)
                )
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
}