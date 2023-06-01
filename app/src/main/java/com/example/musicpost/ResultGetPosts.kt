package com.example.musicpost

data class PostDto(
        var id: Int,
        var originalPoster: UserDto,
        var title: String,
        var description: String,
        var likeCount: Int,
        var music: MusicDto,
        var coordinate: PointDto,
        var commentEntities: List<CommentDto>,
        var address: String,
        var locationName: String,
        var comments: List<CommentDto>
        )

data class UserDto(
        var username: String
)

data class MusicDto(
        var artist: String,
        var songName: String,
        var musicUrl: String
)
data class PointDto(
        var longitude: Double,
        var latitude: Double
)

data class CommentDto(
        var id: Int?,
        var postId: Int,
        var commenter: UserDto,
        var commentText: String
)

data class PinDto(
        var id: Long,
        var owner: UserDto,
        var music: MusicDto,
        var address: String,
        var locationName: String,
        var coordinate: PointDto
)