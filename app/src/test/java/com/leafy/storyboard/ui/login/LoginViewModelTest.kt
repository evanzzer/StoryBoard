package com.leafy.storyboard.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.leafy.storyboard.core.data.utils.Status
import com.leafy.storyboard.core.domain.model.Login
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
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var dataUseCase: DataUseCase
    private lateinit var loginViewModel: LoginViewModel
    private val data = DataDummy.generateDummyLogin()

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(dataUseCase)
    }

    @Test
    fun `When get data should return success and not null`() {
        val expectedResult: Flow<Status<Login>> = flow {
            emit(Status.Success(data))
        }

        `when`(dataUseCase.postLogin("email", "password")).thenReturn(expectedResult)

        val actualResult = loginViewModel.postLogin("email", "password").getOrAwaitValue()
        Mockito.verify(dataUseCase).postLogin("email", "password")
        Assert.assertNotNull(actualResult)
        Assert.assertEquals(data, (actualResult as Status.Success).data)
    }

    @Test
    fun `when get data should return Error when Network Error occurs`() {
        val expectedResult: Flow<Status<Login>> = flow {
            emit(Status.Error("Error"))
        }

        `when`(dataUseCase.postLogin("email", "password")).thenReturn(expectedResult)

        val actualResult = loginViewModel.postLogin("email", "password").getOrAwaitValue()
        Mockito.verify(dataUseCase).postLogin("email", "password")
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Status.Error)
    }

    @Test
    fun `when get data should return Loading when data is fetching from server`() {
        val expectedResult: Flow<Status<Login>> = flow {
            emit(Status.Loading())
        }

        `when`(dataUseCase.postLogin("email", "password")).thenReturn(expectedResult)

        val actualResult = loginViewModel.postLogin("email", "password").getOrAwaitValue()
        Mockito.verify(dataUseCase).postLogin("email", "password")
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Status.Loading)
    }
}