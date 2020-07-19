package net.dejanjokic.arsmovies.ui.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.dejanjokic.arsmovies.data.MovieRepository
import net.dejanjokic.arsmovies.data.domain.DomainItem

class MovieListViewModel @ViewModelInject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableLiveData<MovieListState>()
    val state: LiveData<MovieListState>
        get() = _state

    init {
        if (repository.favoritesEmpty()){
            _state.postValue(MovieListState.Empty)
        } else {
            getFavoriteMovies()
        }
    }

    fun getFavoriteMovies() {
        _state.postValue(MovieListState.Loading)
        viewModelScope.launch {
            repository.getFavoriteMovies().collect { result ->
                result.fold(
                     success = { items -> _state.postValue(MovieListState.Success.Db(items)) },
                     failure =  { t -> _state.postValue(MovieListState.Error(t.localizedMessage ?: "Error")) }
                )
            }
        }
    }

    fun getMovieSearchResults(searchQuery: String) {
        _state.postValue(MovieListState.Loading)
        viewModelScope.launch {
            repository.getMovieSearchResults(searchQuery).collect { result ->
                result.fold(
                    success = { items -> _state.postValue(MovieListState.Success.Network(items)) },
                    failure = { t -> _state.postValue(MovieListState.Error(t.localizedMessage ?: "Error")) }
                )
            }
        }
    }

    fun toggleFavoriteStatus(item: DomainItem.Movie) {
        if (item.isFavorite) {
            repository.unFavoriteMovie(item.id)
            if (repository.favoritesEmpty()) _state.postValue(MovieListState.Empty)
        } else {
            repository.saveMovieToFavorites(item)
        }
    }
}