package com.muhdila.mystoryapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muhdila.mystoryapp.data.helper.UserRepository
import com.muhdila.mystoryapp.data.remote.response.ListStory
import com.muhdila.mystoryapp.data.remote.response.StoryResponse
import com.muhdila.mystoryapp.data.remote.retforit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// ViewModel responsible for handling map-related data and operations
class MapsViewModel(private val userRepo: UserRepository) : ViewModel() {
    // LiveData for holding a list of stories with their locations
    private val _listStoriesLocation = MutableLiveData<List<ListStory>>()
    val listStoriesLocation: LiveData<List<ListStory>> = _listStoriesLocation

    // Fetch user stories based on the user's token
    fun fetchUserStories() {
        userRepo.getUser().observeForever { user ->
            if (user != null) {
                // Make an API request to fetch stories
                val client = ApiConfig.getApiService().getStoryLocation("Bearer " + user.token, 1)
                client.enqueue(object : Callback<StoryResponse> {
                    override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                        val responseBody = response.body()
                        if (response.isSuccessful && responseBody?.message == "Stories fetched successfully") {
                            // Update the LiveData with the fetched list of stories
                            _listStoriesLocation.value = responseBody.listStory
                        }
                    }

                    override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                        // Handle failure, e.g., show an error message or log the error
                    }
                })
            }
        }
    }
}