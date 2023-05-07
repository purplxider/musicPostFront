package com.example.musicpost

data class AccessTokenResponse(
        val access_token: String,
        val token_type: String,
        val expires_in: Long
)