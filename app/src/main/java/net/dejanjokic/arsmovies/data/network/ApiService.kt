package net.dejanjokic.arsmovies.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // https://www.omdbapi.com/?apikey=c49a8a62&s=avengers
    @GET("/")
    suspend fun getMovieSearchResults(
        @Query("s") searchQuery: String,
        @Query("type") type: String = "movie"
    ): MovieSearchResponse

    // https://www.omdbapi.com/?apikey=c49a8a62&i=tt4154796
    @GET("/")
    suspend fun getMovieDetails(
        @Query("i") imdbId: String
    ): MovieDetailsResponse
}