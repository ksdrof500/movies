package br.com.movies_tek.model

const val YOU_TUBE = "YouTube"

data class Video(
        val name: String,
        val key: String,
        val site: String,
        val size: Int,
        val type: String
) {
}
