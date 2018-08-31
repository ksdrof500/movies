package br.com.movies_tek.model

import com.google.gson.annotations.SerializedName
import java.util.*


data class MovieInfo(
        val id: Int,
        val title: String,
        val overview: String,
        @SerializedName("backdrop_path") val backdrop: String,
        @SerializedName("release_date") val releaseDate: Date,
        @SerializedName("poster_path") val poster: String,
        @SerializedName("vote_average") val voteAverage: Double,
        @SerializedName("reviews") val reviewsPage: ReviewsPage,
        @SerializedName("videos") val videosPage: VideosPage
)
