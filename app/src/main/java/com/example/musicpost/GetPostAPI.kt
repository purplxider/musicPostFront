package com.example.musicpost

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GetPostAPI {

    @GET("/api/posts")
    fun getPosts(
            @Query("page") page: Int,
            @Query("count") count : Int
    ): Call<ResultGetPosts>
}