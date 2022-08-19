package com.leafy.storyboard.core.utils

import com.leafy.storyboard.core.data.local.entity.StoryEntity
import com.leafy.storyboard.core.data.remote.response.login.LoginResponse
import com.leafy.storyboard.core.data.remote.response.story.StoryResponse
import com.leafy.storyboard.core.domain.model.Login
import com.leafy.storyboard.core.domain.model.Story

object ObjectMapper {
    fun LoginResponse.toLogin() = Login(name, userId, token)

    fun StoryResponse.toStory() = Story(id, name, description, photoUrl, createdAt, lat, lon)
    fun StoryResponse.toEntity() = StoryEntity(id, name, description, photoUrl, createdAt, lat, lon)
    fun StoryEntity.toStory() = Story(id, name, description, photoUrl, createdAt, lat, lon)
}