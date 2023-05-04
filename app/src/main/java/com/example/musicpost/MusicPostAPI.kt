package com.example.musicpost

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MusicPostAPI {
    @GET("v2/local/search/keyword.json") // Keyword.json의 정보를 받아옴
    fun getSearchKeyword(
            @Header("Authorization") key: String, // 카카오 API 인증키 [필수]
            @Query("music") music: String, // 검색을 원하는 질의어 [필수]
            @Query("poster") poster: String, // 결과 페이지 번호, radius: android.location.Location){}, radius: android.location.Location){}
            @Query("title") title: String,
            @Query("content") content: String
    ): Call<MusicPostResult> // 받아온 정보가 ResultSearchKeyword 클래스의 구조로 담김
}