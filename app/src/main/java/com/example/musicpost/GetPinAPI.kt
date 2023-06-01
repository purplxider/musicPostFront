package com.example.musicpost

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GetPinAPI {

    @GET("api/pin")
    fun getPins(
            @Query("username") username: String
    ): Call<List<PinDto>>
}