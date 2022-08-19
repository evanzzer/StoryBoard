package com.leafy.storyboard.ui.main.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.leafy.storyboard.core.data.utils.Status
import com.leafy.storyboard.core.domain.model.Story
import com.leafy.storyboard.core.domain.usecase.DataUseCase
import com.leafy.storyboard.utils.DataDummy
import com.leafy.storyboard.utils.LiveDataTestUtils.getOrAwaitValue
import com.leafy.storyboard.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var dataUseCase: DataUseCase
    private lateinit var mapsViewModel: MapsViewModel
    private val data = DataDummy.generateDummyStories()

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(dataUseCase)
    }

    @Test
    fun `When get data should return success and not null`() {
        val expectedResult: Flow<Status<List<Story>>> = flow {
            emit(Status.Success(data))
        }

        `when`(dataUseCase.getStoryWithLocation("token")).thenReturn(expectedResult)

        val actualResult = mapsViewModel.getStoryWithLocation("token").getOrAwaitValue()
        Mockito.verify(dataUseCase).getStoryWithLocation("token")
        Assert.assertNotNull(actualResult)
        Assert.assertEquals(data.size, (actualResult as Status.Success).data?.size)
    }

    @Test
    fun `when get data should return Error when Network Error occurs`() {
        val expectedResult: Flow<Status<List<Story>>> = flow {
            emit(Status.Error("Error"))
        }

        `when`(dataUseCase.getStoryWithLocation("token")).thenReturn(expectedResult)

        val actualResult = mapsViewModel.getStoryWithLocation("token").getOrAwaitValue()
        Mockito.verify(dataUseCase).getStoryWithLocation("token")
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Status.Error)
    }

    @Test
    fun `when get data should return Empty when no data has been fetched`() {
        val expectedResult: Flow<Status<List<Story>>> = flow {
            emit(Status.Empty())
        }

        `when`(dataUseCase.getStoryWithLocation("token")).thenReturn(expectedResult)

        val actualResult = mapsViewModel.getStoryWithLocation("token").getOrAwaitValue()
        Mockito.verify(dataUseCase).getStoryWithLocation("token")
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Status.Empty)
    }

    @Test
    fun `when get data should return Loading when data is fetching from server`() {
        val expectedResult: Flow<Status<List<Story>>> = flow {
            emit(Status.Loading())
        }

        `when`(dataUseCase.getStoryWithLocation("token")).thenReturn(expectedResult)

        val actualResult = mapsViewModel.getStoryWithLocation("token").getOrAwaitValue()
        Mockito.verify(dataUseCase).getStoryWithLocation("token")
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Status.Loading)
    }
}