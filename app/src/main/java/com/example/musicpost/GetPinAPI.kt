package com.example.musicpost

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GetPinAPI {

    @GET("api/pin")
    fun getPins(
            @Header("Authorization") authHeader: String,
            @Query("username") username: String
    ): Call<List<PinDto>>
}