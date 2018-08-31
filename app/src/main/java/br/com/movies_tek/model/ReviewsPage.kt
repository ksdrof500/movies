package br.com.movies_tek.model

import com.google.gson.annotations.SerializedName

data class ReviewsPage(
        val page: Int,
        @SerializedName("results") val reviews: List<Review>,
        @SerializedName("total_pages") val totalPages: Int,
        @SerializedName("total_results") val totalResults: Int
)
