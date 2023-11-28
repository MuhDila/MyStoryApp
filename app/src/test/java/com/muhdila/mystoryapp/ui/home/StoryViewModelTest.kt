package com.muhdila.mystoryapp.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.muhdila.mystoryapp.DataDummy
import com.muhdila.mystoryapp.MainDispatcherRule
import com.muhdila.mystoryapp.data.paging.StoryRepository
import com.muhdila.mystoryapp.data.remote.response.ListStory
import com.muhdila.mystoryapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)

class StoryViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStory> = StoryPagingSource.snapshot(dummyStory)
        val expectedStories = MutableLiveData<PagingData<ListStory>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getPagingStory("")).thenReturn(expectedStories)

        val storyViewModel = StoryViewModel(storyRepository)
        val actualStory: PagingData<ListStory> = storyViewModel.stories("").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_ITEM_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when Get Stories Should Not Null and Return No Data` () = runTest {
        val data: PagingData<ListStory> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<ListStory>>()
        expectedStories.value = data
        Mockito.mockStatic(Log::class.java)
        Mockito.`when`(storyRepository.getPagingStory("")).thenReturn(expectedStories)
        val storyViewModel = StoryViewModel(storyRepository)
        val actualStories: PagingData<ListStory> = storyViewModel.stories("").getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_ITEM_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)
        assertEquals(0, differ.snapshot().size)
    }

    class StoryPagingSource : PagingSource<Int, LiveData<List<ListStory>>>() {
        companion object {
            fun snapshot(items: List<ListStory>): PagingData<ListStory> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStory>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStory>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }

    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}