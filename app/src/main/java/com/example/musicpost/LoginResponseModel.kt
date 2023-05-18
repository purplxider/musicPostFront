package com.example.musicpost

data class LoginResponseModel (
        var response: LoginDto
)

data class LoginDto (
        var loginResult: Boolean
)