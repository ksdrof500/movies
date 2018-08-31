package br.com.movies_tek.model

import com.google.gson.annotations.SerializedName

data class MoviesPage(
        val page: Int,
        @SerializedName("results") val movies: List<Movie>,
        @SerializedName("total_pages") val totalPages: Int,
        @SerializedName("total_movies") val totalMovies: Int
)
