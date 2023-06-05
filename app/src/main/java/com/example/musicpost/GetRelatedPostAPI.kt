package com.example.musicpost

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GetRelatedPostAPI {

    @GET("api/posts/{postId}/related-posts")
    fun getRelatedPosts(
            @Header("Authorization") authHeader: String,
            @Path("postId") postId: Int
    ): Call<List<PostDto>>
}