package net.dejanjokic.arsmovies.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import net.dejanjokic.arsmovies.util.Constants.DB.MOVIE_DB_VERSION

@Database(
    entities = [MovieListItemEntity::class, MovieDetailsEntity::class],
    version = MOVIE_DB_VERSION,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}