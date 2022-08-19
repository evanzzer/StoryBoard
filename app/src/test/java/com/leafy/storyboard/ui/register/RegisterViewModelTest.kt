package com.leafy.storyboard.ui.register

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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var dataUseCase: DataUseCase
    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(dataUseCase)
    }

    @Test
    fun `When get data should return success and not null`() {
        val expectedResult: Flow<Status<String>> = flow {
            emit(Status.Success("Success"))
        }

        `when`(dataUseCase.postRegister("name","email", "password"))
            .thenReturn(expectedResult)

        val actualResult = registerViewModel.postRegister("name","email", "password")
            .getOrAwaitValue()
        Mockito.verify(dataUseCase).postRegister("name", "email", "password")
        Assert.assertNotNull(actualResult)
        Assert.assertEquals("Success", (actualResult as Status.Success).data)
    }

    @Test
    fun `when get data should return Error when Network Error occurs`() {
        val expectedResult: Flow<Status<String>> = flow {
            emit(Status.Error("Error"))
        }

        `when`(dataUseCase.postRegister("name","email", "password"))
            .thenReturn(expectedResult)

        val actualResult = registerViewModel.postRegister("name","email", "password")
            .getOrAwaitValue()
        Mockito.verify(dataUseCase).postRegister("name", "email", "password")
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Status.Error)
    }

    @Test
    fun `when get data should return Loading when data is fetching from server`() {
        val expectedResult: Flow<Status<String>> = flow {
            emit(Status.Loading())
        }

        `when`(dataUseCase.postRegister("name","email", "password"))
            .thenReturn(expectedResult)

        val actualResult = registerViewModel.postRegister("name","email", "password")
            .getOrAwaitValue()
        Mockito.verify(dataUseCase).postRegister("name", "email", "password")
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Status.Loading)
    }
}