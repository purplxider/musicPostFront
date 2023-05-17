package com.example.musicpost


data class ResultGetPosts (
        var posts: List<PostDto>
        )

data class PostDto(
        var id: Int,
        var originalPoster: UserDto,
        var title: String,
        var description: String,
        var likeCount: Int,
        var music: MusicDto,
        var coordinate: Point,
        var commentEntities: List<CommentDto>,
        var address: String,
        var location_name: String,
        var comments: List<CommentDto>

        )

data class UserDto(
        var username: String
)

data class MusicDto(
        var artist: String,
        var songName: String,
        var music_url: String
)
data class Point(
        var longitude: Double,
        var latitude: Double
)

data class CommentDto(
        var id: Int,
        var postId: Int,
        var commenter: UserDto,
        var commentText: String
)