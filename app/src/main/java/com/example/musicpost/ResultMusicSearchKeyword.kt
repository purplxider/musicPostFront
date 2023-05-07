package com.example.musicpost

data class ResultMusicSearchKeyword(
        var tracks: TrackResults
)

data class TrackResults(
        val items: List<Track>
)

data class Track(
        val id: String,
        val name: String,
        val artists: List<Artist>,
        val preview_url: String
)

data class Artist(
        val name: String
)