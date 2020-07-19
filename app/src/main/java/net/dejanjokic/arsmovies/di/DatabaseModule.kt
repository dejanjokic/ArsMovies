package net.dejanjokic.arsmovies.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import net.dejanjokic.arsmovies.data.db.MovieDao
import net.dejanjokic.arsmovies.data.db.MovieDatabase
import net.dejanjokic.arsmovies.util.Constants.DB.MOVIE_DB_NAME
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(@ApplicationContext context: Context): MovieDatabase =
        Room.databaseBuilder(context, MovieDatabase::class.java, MOVIE_DB_NAME).build()

    @Provides
    @Singleton
    fun provideMovieDao(db: MovieDatabase): MovieDao = db.movieDao()
}