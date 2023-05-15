package com.example.musicpost

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PostPostAPI {
    @POST("/api/posts")
    fun postPost(
            @Body requestModel: PostRequestModel
    ): Call<PostResponseModel>
}