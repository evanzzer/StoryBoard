package com.leafy.storyboard.core.data.remote.response.story

import com.google.gson.annotations.SerializedName

data class ListStoryResponse(
    @field:SerializedName("listStory")
    val listStory: List<StoryResponse>? = null,

    @field:SerializedName("error")
    val error: Boolean = true,

    @field:SerializedName("message")
    var message: String = "Unknown error has occurred."
)
