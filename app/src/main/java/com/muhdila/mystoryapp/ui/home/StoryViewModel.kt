package com.muhdila.mystoryapp.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.muhdila.mystoryapp.data.paging.Injection
import com.muhdila.mystoryapp.data.remote.response.ListStory
import com.muhdila.mystoryapp.data.paging.StoryRepository

// ViewModel responsible for handling and providing a list of stories
class StoryViewModel(private val storiesRepository: StoryRepository) : ViewModel() {

    // Function to retrieve and observe a list of stories using paging
    fun stories(header: String): LiveData<PagingData<ListStory>> =
        storiesRepository.getPagingStory(header).cachedIn(viewModelScope)

    // Factory class for creating instances of StoryViewModel
    class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                // Create a StoryViewModel instance with the provided context
                return StoryViewModel(Injection.provideRepository(context)) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}