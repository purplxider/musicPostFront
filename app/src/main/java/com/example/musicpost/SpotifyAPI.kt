package com.example.musicpost

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyAPI {
    @GET("search")
    fun searchTracks(
            @Header("Authorization") token: String, // Spotify API 인증키
            @Query("q") query: String,
            @Query("type") type: String = "track"
    ): Call<ResultMusicSearchKeyword>
}