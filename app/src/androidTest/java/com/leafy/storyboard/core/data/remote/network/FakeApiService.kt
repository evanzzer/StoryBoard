package com.leafy.storyboard.core.data.remote.network

import com.leafy.storyboard.core.data.remote.request.LoginRequest
import com.leafy.storyboard.core.data.remote.request.RegisterRequest
import com.leafy.storyboard.core.data.remote.response.DefaultResponse
import com.leafy.storyboard.core.data.remote.response.login.LoginMessageResponse
import com.leafy.storyboard.core.data.remote.response.login.LoginResponse
import com.leafy.storyboard.core.data.remote.response.story.ListStoryResponse
import com.leafy.storyboard.core.data.remote.response.story.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {
    override suspend fun postRegister(body: RegisterRequest): DefaultResponse {
        return DefaultResponse(false, "Success")
    }

    override suspend fun postLogin(body: LoginRequest): LoginMessageResponse {
        return LoginMessageResponse(
            LoginResponse("name", "userid", "token"),
            false, "Success"
        )
    }

    override suspend fun postNewStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        latitude: Double,
        longitude: Double
    ): DefaultResponse {
        return DefaultResponse(false, "Success")
    }

    override suspend fun getStoryList(
        token: String,
        page: Int,
        size: Int,
        location: Int
    ): ListStoryResponse {
        val storyList = ArrayList<StoryResponse>()
        for (i in 0..100) {
            val story = StoryResponse(
                "ID $i",
                "Title $i",
                "A description for title $i",
                "https://example.com/story/$i",
                "2022-02-22T22:22:22Z",
                i.toDouble(),
                i.toDouble()
            )
            storyList.add(story)
        }
        return ListStoryResponse(storyList, false, "Success")
    }

}