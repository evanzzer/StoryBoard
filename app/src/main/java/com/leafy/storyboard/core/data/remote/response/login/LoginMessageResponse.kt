package com.leafy.storyboard.core.data.remote.response.login

import com.google.gson.annotations.SerializedName

data class LoginMessageResponse(

    @field:SerializedName("loginResult")
    val loginResult: LoginResponse? = null,

    @field:SerializedName("error")
    val error: Boolean = true,

    @field:SerializedName("message")
    var message: String = "Unknown error has occurred."
)
