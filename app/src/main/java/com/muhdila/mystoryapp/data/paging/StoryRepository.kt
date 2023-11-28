package com.muhdila.mystoryapp.data.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.muhdila.mystoryapp.data.remote.response.ListStory
import com.muhdila.mystoryapp.data.remote.retforit.ApiService

class StoryRepository(private val apiService: ApiService) {
    fun getPagingStory(header: String) : LiveData<PagingData<ListStory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = {
                StoryPaging(apiService, header)
            }
        ).liveData
    }
}