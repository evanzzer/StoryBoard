package com.leafy.storyboard.core.data.remote.paging

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leafy.storyboard.core.data.local.entity.StoryEntity
import com.leafy.storyboard.core.data.local.room.StoryDatabase
import com.leafy.storyboard.core.data.remote.network.FakeApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {
    private val mockApi = FakeApiService()
    private val mockDb = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }

    @Test
    fun refreshLoadReturnSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator("token", mockDb, mockApi)
        val pagingState = PagingState<Int, StoryEntity>(listOf(), null, PagingConfig(10), 10)

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
        Assert.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}