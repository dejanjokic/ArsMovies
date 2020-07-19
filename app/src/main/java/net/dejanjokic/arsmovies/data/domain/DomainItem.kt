package net.dejanjokic.arsmovies.data.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

sealed class DomainItem {
    abstract val id: String

    data class Header(val year: Int) : DomainItem() {
        override val id: String = "$year${UUID.randomUUID()}"
    }

    data class Movie(
        val imdbId: String,
        val title: String,
        val year: Int,
        val poster: String?,
        var isFavorite: Boolean = false
    ) : DomainItem() {
        override val id: String = imdbId
    }
}

@Parcelize
data class DomainDetails(
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
) : Parcelable