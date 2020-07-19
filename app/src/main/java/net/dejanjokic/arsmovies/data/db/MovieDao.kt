package net.dejanjokic.arsmovies.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.dejanjokic.arsmovies.util.Constants.DB.MOVIE_DETAILS_TABLE_NAME
import net.dejanjokic.arsmovies.util.Constants.DB.MOVIE_TABLE_NAME

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieListItem(item: MovieListItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetailsItem(item: MovieDetailsEntity)

    @Query("SELECT * FROM $MOVIE_TABLE_NAME")
    suspend fun getAllMovieListItems(): List<MovieListItemEntity>

    @Query("SELECT * FROM $MOVIE_TABLE_NAME")
    fun getAllMovieListItemsFlow(): Flow<List<MovieListItemEntity>>

    @Query("SELECT * FROM $MOVIE_TABLE_NAME where imdbId = :id")
    suspend fun getMovieListItem(id: String): MovieListItemEntity

    @Query("SELECT * FROM $MOVIE_DETAILS_TABLE_NAME WHERE imdbId = :id")
    suspend fun getMovieDetailsItem(id: String): MovieDetailsEntity

    @Query("DELETE FROM $MOVIE_TABLE_NAME WHERE imdbId = :id")
    suspend fun deleteMovieListItem(id: String)

    @Query("DELETE FROM $MOVIE_DETAILS_TABLE_NAME WHERE imdbId = :id")
    suspend fun deleteMovieDetailsItem(id: String)

    @Transaction
    suspend fun insertMovie(listItem: MovieListItemEntity, detailsItem: MovieDetailsEntity) {
        insertMovieListItem(listItem)
        insertMovieDetailsItem(detailsItem)
    }

    @Transaction
    suspend fun deleteMovie(id: String) {
        deleteMovieListItem(id)
        deleteMovieDetailsItem(id)
    }
}