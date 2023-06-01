package com.example.musicpost

data class PinRequestModel(
        var owner: UserDto,
        var music: MusicDto,
        var address: String,
        var locationName: String,
        var coordinate: PointDto
)
