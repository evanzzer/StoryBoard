package com.leafy.storyboard.ui.main.insert

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.leafy.storyboard.core.data.utils.Status
import com.leafy.storyboard.core.domain.usecase.DataUseCase
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
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class NewStoryViewModelTest {

    @get:Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var dataUseCase: DataUseCase
    private lateinit var newStoryViewModel: NewStoryViewModel

    @Before
    fun setUp() {
        newStoryViewModel = NewStoryViewModel(dataUseCase)
    }

    @Test
    fun `When get data should return success and not null`() {
        val expectedResult: Flow<Status<String>> = flow {
            emit(Status.Success("Success"))
        }

        `when`(dataUseCase.postNewStory("token", File("string"), "description", 0.0, 0.0))
            .thenReturn(expectedResult)

        val actualResult = newStoryViewModel.postNewStory("token", File("string"), "description", 0.0, 0.0)
            .getOrAwaitValue()
        Mockito.verify(dataUseCase).postNewStory("token", File("string"), "description", 0.0, 0.0)
        Assert.assertNotNull(actualResult)
        Assert.assertEquals("Success", (actualResult as Status.Success).data)
    }

    @Test
    fun `when get data should return Error when Network Error occurs`() {
        val expectedResult: Flow<Status<String>> = flow {
            emit(Status.Error("Error"))
        }

        `when`(dataUseCase.postNewStory("token", File("string"), "description", 0.0, 0.0))
            .thenReturn(expectedResult)

        val actualResult = newStoryViewModel.postNewStory("token", File("string"), "description", 0.0, 0.0)
            .getOrAwaitValue()
        Mockito.verify(dataUseCase).postNewStory("token", File("string"), "description", 0.0, 0.0)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Status.Error)
    }

    @Test
    fun `when get data should return Loading when data is fetching from server`() {
        val expectedResult: Flow<Status<String>> = flow {
            emit(Status.Loading())
        }

        `when`(dataUseCase.postNewStory("token", File("string"), "description", 0.0, 0.0))
            .thenReturn(expectedResult)

        val actualResult = newStoryViewModel.postNewStory("token", File("string"), "description", 0.0, 0.0)
            .getOrAwaitValue()
        Mockito.verify(dataUseCase).postNewStory("token", File("string"), "description", 0.0, 0.0)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Status.Loading)
    }
}