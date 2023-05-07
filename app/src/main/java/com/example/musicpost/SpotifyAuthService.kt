package com.example.musicpost

import retrofit2.http.*

interface SpotifyAuthService {
    @POST("/api/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    suspend fun getAccessToken(
            @Field("grant_type") grantType: String,
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String
    ): AccessTokenResponse
}