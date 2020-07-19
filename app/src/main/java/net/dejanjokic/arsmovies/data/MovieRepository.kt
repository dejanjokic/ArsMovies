package net.dejanjokic.arsmovies.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.dejanjokic.arsmovies.data.db.MovieDao
import net.dejanjokic.arsmovies.data.domain.DomainDetails
import net.dejanjokic.arsmovies.data.domain.DomainItem
import net.dejanjokic.arsmovies.data.network.ApiService
import timber.log.Timber
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val api: ApiService,
    private val dao: MovieDao,
    private val favDataSource: FavoritesDataSource
) {

    fun getFavoriteMovies(): Flow<SimpleResult<List<DomainItem>>> = flow {
        try {
            dao.getAllMovieListItemsFlow().collect {
                val items = MovieMapper.entityListToDomain(it)
                emit(Result.Success(items))
        }
        } catch (t: Throwable) {
            Timber.e("Error: ${t.localizedMessage}")
            emit(Result.Failure(t))
        }
    }.flowOn(Dispatchers.IO)

    fun getMovieSearchResults(searchQuery: String = "Godfather"): Flow<SimpleResult<List<DomainItem>>> = flow {
        val favoriteIds = favDataSource.getFavoriteIds()
            try {
                val searchResponse = api.getMovieSearchResults(searchQuery)
                when (searchResponse.response) {
                    "True" -> {
                        Timber.d("Successful search response for query $searchQuery")
                        val items = MovieMapper.movieSearchResponseToDomain(searchResponse.items!!)
                        items.forEach {
                            if (favoriteIds.contains(it.id)) {
                                (it as DomainItem.Movie).isFavorite = true
                            }
                        }
                        emit(Result.Success(items))
                    }
                    "False" -> {
                        Timber.d("Network error: ${searchResponse.errorMessage}")
                        emit(Result.Failure(Throwable(searchResponse.errorMessage)))
                    }
                    else -> {
                        Timber.e("Unknown error")
                        emit(Result.Failure(UnknownError()))
                    }
                }
            } catch (t: Throwable) {
                Timber.e("Error ${t.localizedMessage}")
                emit(Result.Failure(t))
            }
        }.flowOn(Dispatchers.IO)

    fun getMovieDetails(id: String): Flow<SimpleResult<DomainDetails>> = flow {
        val isFavorite = favDataSource.getFavoriteIds().contains(id)
        if (isFavorite) {

            try {
                Timber.d("DB details success")
                val entity = dao.getMovieDetailsItem(id)
                emit(Result.Success(MovieMapper.movieDetailsEntityToDomain(entity)))

            } catch (t: Throwable) {
                Timber.e("Error ${t.localizedMessage}")
                emit(Result.Failure(t))
            }

        } else {

            try {
                val detailsResponse = api.getMovieDetails(id)
                when (detailsResponse.response) {
                    "True" -> {
                        Timber.d("Details request successful for $id")
                        emit(Result.Success(MovieMapper.movieDetailsResponseToDomain(detailsResponse)))
                    }
                    "False" -> {
                        Timber.d("Network error: ${detailsResponse.response}")
                        emit(Result.Failure(Throwable(detailsResponse.response)))
                    }
                    else -> {
                        Timber.e("Unknown error")
                        emit(Result.Failure(Throwable(detailsResponse.response)))
                    }
                }
            } catch (t: Throwable) {
                Timber.e("Error ${t.localizedMessage}")
                emit(Result.Failure(t))
            }
        }
    }.flowOn(Dispatchers.IO)
    
    fun saveMovieToFavorites(item: DomainItem.Movie) = CoroutineScope(Dispatchers.IO).launch {
        Timber.d("Saving ${item.title} to favorites")
        favDataSource.saveIdToFavorites(item.imdbId)
        val response = api.getMovieDetails(item.imdbId)
        val listItem = MovieMapper.movieDomainItemToEntity(item).also { it.isFavorite = true }
        val detailsItem = MovieMapper.movieDetailsResponseToEntity(response).also { it.isFavorite = true }
        dao.insertMovie(listItem, detailsItem)
    }

    fun saveMovieToFavorites(item: DomainDetails) = CoroutineScope(Dispatchers.IO).launch {
        Timber.d("Saving ${item.title} to favorites")
        favDataSource.saveIdToFavorites(item.imdbId)
        val listItem = MovieMapper.movieDomainItemToEntity(
            DomainItem.Movie(
                imdbId = item.imdbId,
                title = item.title,
                year = item.year,
                poster = item.posterUrl,
                isFavorite = true
            )
        )
        val detailsItem = MovieMapper.movieDetailsDomainToEntity(item.apply { isFavorite = true })
        dao.insertMovie(listItem, detailsItem)
    }

    fun unFavoriteMovie(id: String) = CoroutineScope(Dispatchers.IO).launch {
        Timber.d("Removing $id from favorites")
        dao.deleteMovie(id)
        favDataSource.removeIdFromFavorites(id)
    }

    fun favoritesEmpty(): Boolean = favDataSource.getFavoriteIds().isEmpty()
}
