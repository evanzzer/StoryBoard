package com.leafy.storyboard.core.data.remote.response.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("token")
    val token: String? = null
)
