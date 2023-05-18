package com.example.musicpost

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpAPI {
    @POST("api/users")
    fun signUp(
            @Body signUpRequest: SignUpRequestModel
    ):Call<PostResponseModel>
}