package com.muhdila.mystoryapp.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muhdila.mystoryapp.data.helper.UserRepository
import com.muhdila.mystoryapp.data.helper.reduceFileImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.muhdila.mystoryapp.data.remote.response.DataResponse
import com.muhdila.mystoryapp.data.remote.retforit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

// ViewModel responsible for handling the story upload process
class UploadViewModel(
    private val apiService: ApiService,
    private val userRepository: UserRepository
) : ViewModel() {
    // LiveData to observe loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to observe the upload result
    private val _uploadResult = MutableLiveData<UploadResult>()
    val uploadResult: LiveData<UploadResult> = _uploadResult

    // Sealed class to represent different upload results
    sealed class UploadResult {
        data object Success : UploadResult()
        data object Error : UploadResult()
        data object NetworkError : UploadResult()
    }

    // Function to initiate the upload process
    fun uploadStory(file: File?, description: String) {
        if (file == null) {
            _uploadResult.value = UploadResult.Error
            return
        }

        _isLoading.value = true

        // Reduce the image file before uploading
        val reducedFile = reduceFileImage(file)

        // Create a request body for the description
        val descriptionBody = description.toRequestBody("text/plain".toMediaType())

        // Create a request body for the image file
        val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

        // Create a MultipartBody.Part for the image file
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            reducedFile.name,
            requestImageFile
        )

        // Observe the user to get the authorization token
        userRepository.getUser().observeForever { user ->
            if (user != null) {
                // Construct the client for uploading the story
                val client = apiService.uploadStory("Bearer " + user.token, imageMultipart, descriptionBody)

                // Make the upload request
                client.enqueue(object : Callback<DataResponse> {
                    override fun onResponse(
                        call: Call<DataResponse>,
                        response: Response<DataResponse>
                    ) {
                        _isLoading.value = false
                        val responseBody = response.body()

                        if (response.isSuccessful && responseBody?.message == "Story created successfully") {
                            _uploadResult.value = UploadResult.Success
                        } else {
                            _uploadResult.value = UploadResult.Error
                        }
                    }

                    override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                        _isLoading.value = false
                        _uploadResult.value = UploadResult.NetworkError
                    }
                })
            }
        }
    }
}