package com.leafy.storyboard.core.data.remote.network

import com.leafy.storyboard.core.data.remote.request.LoginRequest
import com.leafy.storyboard.core.data.remote.request.RegisterRequest
import com.leafy.storyboard.core.data.remote.response.DefaultResponse
import com.leafy.storyboard.core.data.remote.response.login.LoginMessageResponse
import com.leafy.storyboard.core.data.remote.response.story.ListStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("register")
    suspend fun postRegister(
        @Body body: RegisterRequest
    ): DefaultResponse

    @POST("login")
    suspend fun postLogin(
        @Body body: LoginRequest
    ): LoginMessageResponse

    @Multipart
    @POST("stories")
    suspend fun postNewStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part, // photo
        @Part("description") description: RequestBody,
        @Part("lat") latitude: Double,
        @Part("lon") longitude: Double
    ): DefaultResponse

    @GET("stories")
    suspend fun getStoryList(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int = 0
    ): ListStoryResponse
}