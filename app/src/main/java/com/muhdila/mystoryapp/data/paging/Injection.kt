package com.muhdila.mystoryapp.data.paging

import android.content.Context
import com.muhdila.mystoryapp.data.helper.StoryData
import com.muhdila.mystoryapp.data.remote.retforit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        StoryData.getDataStory(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService)
    }
}