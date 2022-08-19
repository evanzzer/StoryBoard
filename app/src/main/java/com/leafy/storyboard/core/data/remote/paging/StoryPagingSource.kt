package com.leafy.storyboard.core.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.leafy.storyboard.core.data.remote.network.ApiService
import com.leafy.storyboard.core.data.remote.response.story.StoryResponse

// Archive for Education Purpose: Not to be deleted
@Suppress("unused")
class StoryPagingSource(private val token: String, private val apiService: ApiService) : PagingSource<Int, StoryResponse>() {
    override fun getRefreshKey(state: PagingState<Int, StoryResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse> {
        return try {
            val position = params.key ?: 1
            val responseData = apiService.getStoryList(token, position, params.loadSize, 0)

            LoadResult.Page(
                data = responseData.listStory ?: ArrayList(),
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}