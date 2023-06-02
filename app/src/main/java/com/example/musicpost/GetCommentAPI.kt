package com.example.musicpost

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GetCommentAPI {

    @GET("api/comments")
    fun getComments(
            @Header("Authorization") authHeader: String,
            @Query("postId") postId: Int
    ): Call<List<CommentDto>>
}