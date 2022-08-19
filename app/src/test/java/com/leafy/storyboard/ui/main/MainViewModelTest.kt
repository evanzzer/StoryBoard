package com.leafy.storyboard.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import com.leafy.storyboard.core.domain.usecase.DataUseCase
import com.leafy.storyboard.utils.DataDummy
import com.leafy.storyboard.utils.LiveDataTestUtils.getOrAwaitValue
import com.leafy.storyboard.utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
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
class MainViewModelTest {
    @get:Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var dataUseCase: DataUseCase
    private lateinit var mainViewModel: MainViewModel
    private val data = DataDummy.generateDummyStories()

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(dataUseCase)
    }

    @Test
    fun `When get data should not null`() = runTest {
        val pagingData = StoryPagingSource.snapshot(data)
        val expectedValue = flow { emit(pagingData) }

        `when`(dataUseCase.getStoryList("token")).thenReturn(expectedValue)

        val actualValue = mainViewModel.getStoryList("token").getOrAwaitValue()
        Mockito.verify(dataUseCase).getStoryList("token")

        val noopListUpdateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {}
            override fun onRemoved(position: Int, count: Int) {}
            override fun onMoved(fromPosition: Int, toPosition: Int) {}
            override fun onChanged(position: Int, count: Int, payload: Any?) {}
        }

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualValue)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(data, differ.snapshot())
        Assert.assertEquals(data.size, differ.snapshot().size)
        Assert.assertEquals(data[0].id, differ.snapshot()[0]?.id)
    }
}