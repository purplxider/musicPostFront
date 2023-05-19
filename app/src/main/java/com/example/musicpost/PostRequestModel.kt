package com.example.musicpost

data class PostRequestModel(
    var originalPoster: UserDto,
    var title: String,
    var description: String,
    var music: MusicDto,
    var coordinate: PointDto,
    var address: String,
    var locationName: String
)
