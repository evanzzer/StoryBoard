package com.leafy.storyboard.ui.main

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.leafy.storyboard.core.data.remote.network.ApiService
import com.leafy.storyboard.core.data.remote.response.story.StoryResponse
import com.leafy.storyboard.core.domain.model.Story

// Archive for Education Purpose: Not to be deleted
@Suppress("unused")
class StoryPagingSource(private val token: String, private val apiService: ApiService) : PagingSource<Int, StoryResponse>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryResponse>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}