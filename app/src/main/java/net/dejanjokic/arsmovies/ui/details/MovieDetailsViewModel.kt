package net.dejanjokic.arsmovies.ui.details

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.dejanjokic.arsmovies.data.MovieRepository
import net.dejanjokic.arsmovies.data.domain.DomainDetails

class MovieDetailsViewModel @ViewModelInject constructor(
    private val repository: MovieRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableLiveData<MovieDetailsState> = savedStateHandle.getLiveData(STATE_KEY)
    val state: LiveData<MovieDetailsState>
        get() = _state

    fun getMovieDetails(id: String) = viewModelScope.launch {
        _state.postValue(MovieDetailsState.Loading)
        repository.getMovieDetails(id).collect {
            it.fold(
                { item -> _state.postValue(MovieDetailsState.Success(item)) },
                { t -> _state.postValue(MovieDetailsState.Error(t.localizedMessage ?: "Error")) }
            )
        }
    }

    fun toggleFavoriteStatus(item: DomainDetails) {
        if (item.isFavorite) {
            repository.unFavoriteMovie(item.imdbId)
        } else {
            repository.saveMovieToFavorites(item)
        }
    }

    companion object {
        private const val STATE_KEY = "movie_details_state"
    }
}