package com.example.musicpost

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PostCommentAPI {
    @POST("api/comments")
    fun commentPost(
            @Header("Authorization") authHeader: String,
            @Body requestModel: CommentDto
    ): Call<PostResponseModel>
}