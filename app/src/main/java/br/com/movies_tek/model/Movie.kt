package br.com.movies_tek.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Movie(
        val id: Int,
        @SerializedName("backdrop_path") val backdrop: String,
        val overview: String,
        @SerializedName("release_date") val releaseDate: Date,
        @SerializedName("poster_path") val poster: String,
        val title: String,
        @SerializedName("vote_average") val voteAverage: Double
)
