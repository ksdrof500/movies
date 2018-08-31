package br.com.movies_tek.model

import com.google.gson.annotations.SerializedName

data class VideosPage(@SerializedName("results") val videos: List<Video>)
