package com.leafy.storyboard.core.data.remote.response

import com.google.gson.annotations.SerializedName

data class DefaultResponse(
    @field:SerializedName("error")
    val error: Boolean = true,

    @field:SerializedName("message")
    var message: String = "Unknown error has occurred."
)
