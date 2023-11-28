package com.muhdila.mystoryapp.data.remote.retforit

import com.muhdila.mystoryapp.data.remote.response.LoginResponse
import com.muhdila.mystoryapp.data.remote.response.DataResponse
import com.muhdila.mystoryapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Register a user with name, email, and password
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<DataResponse>

    // Log in a user with email and password
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    // Get a list of stories
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") header: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    // Upload a story with authorization header, file, and description
    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<DataResponse>

    // Location
    @GET("stories")
    fun getStoryLocation(
        @Header("Authorization") header: String,
        @Query("location") location: Int
    ) : Call<StoryResponse>
}