package net.dejanjokic.arsmovies.data.network

import com.squareup.moshi.Json

data class MovieSearchResponse(
    @field:Json(name = "Search") val items: List<MovieDto>? = emptyList(),
    @field:Json(name = "totalResults") val totalResults: Int? = 0,
    @field:Json(name = "Response") val response: String,
    @field:Json(name = "Error") val errorMessage: String? = ""
)

data class MovieDto(
    @field:Json(name = "imdbID") val imdbId: String,
    @field:Json(name = "Title") val title: String,
    @field:Json(name = "Year") val year: Int,
    @field:Json(name = "Poster") val posterUrl: String?
)