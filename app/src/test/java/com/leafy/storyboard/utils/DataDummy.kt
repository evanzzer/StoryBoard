package com.leafy.storyboard.utils

import com.leafy.storyboard.core.domain.model.Login
import com.leafy.storyboard.core.domain.model.Story

object DataDummy {
    fun generateDummyStories(): List<Story> {
        val storyList = ArrayList<Story>()
        for (i in 0..10) {
            val story = Story(
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
        return storyList
    }

    fun generateDummyLogin(): Login {
        return Login(
            "Dummy",
            "dummy-id",
            "token"
        )
    }
}