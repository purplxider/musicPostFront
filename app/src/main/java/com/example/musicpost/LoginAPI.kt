package com.example.musicpost

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface LoginAPI {
    @GET("/")
    fun login(
            @Header("Authorization") authHeader: String
    ): Call<PostResponseModel>
}