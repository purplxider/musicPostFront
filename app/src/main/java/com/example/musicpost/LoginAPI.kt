package com.example.musicpost

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginAPI {
    @POST("api/users/login")
    fun login(
            @Body loginRequest: SignUpRequestModel
    ): Call<LoginResponseModel>
}