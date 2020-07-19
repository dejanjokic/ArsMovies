package net.dejanjokic.arsmovies.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import net.dejanjokic.arsmovies.util.Constants.DB.MOVIE_DETAILS_TABLE_NAME

@Entity(tableName = MOVIE_DETAILS_TABLE_NAME)
data class MovieDetailsEntity(
    @PrimaryKey
    val imdbId: String,
    val title: String,
    val year: Int,
    val posterUrl: String?,
    val rated: String? = "",
    val releaseDate: String? = "",
    val runtime: String? = "",
    val genres: String? = "",
    val director: String? = "",
    val writer: String? = "",
    val actors: String? = "",
    val plot: String? = "",
    val language: String? = "",
    val country: String? = "",
    val awards: String? = "",
    val metaScore: String? = "",
    val imdbRating: String? = "",
    val imdbVotes: String? = "",
    val boxOffice: String? = "",
    val production: String? = "",
    val website: String? = "",
    var isFavorite: Boolean = false
)