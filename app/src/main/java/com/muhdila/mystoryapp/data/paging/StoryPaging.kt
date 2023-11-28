package com.muhdila.mystoryapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.muhdila.mystoryapp.data.remote.response.ListStory
import com.muhdila.mystoryapp.data.remote.retforit.ApiService

class StoryPaging(private val apiService: ApiService, private val header: String) : PagingSource<Int, ListStory>() {
    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(header, page, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if(page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if(responseData.listStory.isEmpty()) null else page + 1
            )
        } catch(exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}