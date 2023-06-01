package com.example.musicpost

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Path

interface DeletePinAPI {
    @DELETE("api/pin/{id}")
    fun deletePin(
            @Header("Authorization") authHeader: String,
            @Path("id") id: Long
    ): Call<Void>
}