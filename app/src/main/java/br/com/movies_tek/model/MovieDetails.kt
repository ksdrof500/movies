package br.com.movies_tek.model

import java.util.*

data class MovieDetails(
        val isFav: Boolean,
        val id: Int,
        val title: String,
        val overview: String,
        val releaseDate: Date,
        val voteAverage: Double,
        val poster: String,
        val backdrop: String,
        val videos: List<Video>,
        val reviews: List<Review>
)