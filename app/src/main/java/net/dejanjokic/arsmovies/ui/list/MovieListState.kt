package net.dejanjokic.arsmovies.ui.list

import net.dejanjokic.arsmovies.data.domain.DomainItem

sealed class MovieListState {
    sealed class Success : MovieListState() {
        data class Db(val items: List<DomainItem>) : Success()
        data class Network(val items: List<DomainItem>) : Success()
    }
    data class Error(val message: String) : MovieListState()
    object Empty : MovieListState()
    object Loading : MovieListState()
}