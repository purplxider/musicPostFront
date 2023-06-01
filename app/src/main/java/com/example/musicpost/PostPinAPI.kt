package com.example.musicpost

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PostPinAPI {
    @POST("api/pin")
    fun pinPost(
            @Header("Authorization") authHeader: String,
            @Body requestModel: PinRequestModel
    ): Call<PostResponseModel>
}