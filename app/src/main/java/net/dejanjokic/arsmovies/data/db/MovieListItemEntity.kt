package net.dejanjokic.arsmovies.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import net.dejanjokic.arsmovies.util.Constants.DB.MOVIE_TABLE_NAME

@Entity(tableName = MOVIE_TABLE_NAME)
data class MovieListItemEntity(
    @PrimaryKey
    val imdbId: String,
    val title: String,
    val year: Int,
    val posterUrl: String?,
    var isFavorite: Boolean = false
)