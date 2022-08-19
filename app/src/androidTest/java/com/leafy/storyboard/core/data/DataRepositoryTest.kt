package com.leafy.storyboard.core.data

import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leafy.storyboard.core.data.local.room.StoryDatabase
import com.leafy.storyboard.core.data.remote.RemoteDataSource
import com.leafy.storyboard.core.data.remote.RemoteInterface
import com.leafy.storyboard.core.data.remote.network.FakeApiService
import com.leafy.storyboard.core.data.remote.request.LoginRequest
import com.leafy.storyboard.core.data.utils.Status
import com.leafy.storyboard.core.domain.model.Login
import com.leafy.storyboard.core.domain.model.Story
import com.leafy.storyboard.core.utils.ObjectMapper.toLogin
import com.leafy.storyboard.core.utils.ObjectMapper.toStory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class DataRepositoryTest {

    private val mockApi = FakeApiService()
    private val mockDb = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()
    private lateinit var remoteDataSource: RemoteInterface
    private lateinit var dataRepository: DataSource

    @Before
    fun setUp() {
        remoteDataSource = RemoteDataSource(mockDb, mockApi)
        dataRepository = DataRepository(remoteDataSource)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }

    @Test
    fun postRegister() = runTest {
        val actualResult = dataRepository.postRegister("n", "e", "p")

        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult.last() is Status.Success)
        Assert.assertEquals(actualResult.last().data, "Success")
    }

    @Test
    fun postLogin() = runTest {
        val actualResult = dataRepository.postLogin("e", "p")

        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult.last() is Status.Success)
        Assert.assertTrue(actualResult.last().data is Login)
        Assert.assertEquals(actualResult.last().data,
            mockApi.postLogin(LoginRequest("e", "p")).loginResult?.toLogin())
    }

    @Test
    fun postNewStory() = runTest {
        val actualResult = dataRepository.postNewStory("", File(""), "", 0.0, 0.0)

        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult.last() is Status.Success)
        Assert.assertEquals(actualResult.last().data, "Success")
    }

    @Test
    fun getStoryList() = runTest {
        // Check for not null since it will be tested further in StoryRemoteMediatorTest.kt
        val actualResult = dataRepository.getStoryList("t")

        Assert.assertNotNull(actualResult)
    }

    @Test
    fun getStoryWithLocation() = runTest {
        val actualResult = dataRepository.getStoryWithLocation("t")

        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult.last() is Status.Success)
        Assert.assertTrue(actualResult.last().data is List<Story>)
        Assert.assertEquals(actualResult.last().data,
            mockApi.getStoryList("t", 0, 0, 0).listStory?.map { it.toStory() })
    }
}