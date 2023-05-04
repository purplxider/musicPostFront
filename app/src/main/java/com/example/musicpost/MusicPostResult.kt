package com.example.musicpost

data class MusicPostResult(
        var total_count: Int, // 포스트 개수
        var documents: MutableList<Post> // 검색 결과
)

data class Post(
        var music: String, // 음악 URL
        var poster: String, // 포스트를 올린 유저 아이디
        var title: String, // 포스트 제목
        var location: String, // 포스팅 한 장소명
        var address: String, // 포스팅한 장소의 주소
        var content: String, // 포스트 내용
        var x: String, // X 좌표값 혹은 longitude
        var y: String // Y 좌표값 혹은 latitude
)