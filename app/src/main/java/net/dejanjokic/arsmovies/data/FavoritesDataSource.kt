package net.dejanjokic.arsmovies.data

import android.content.SharedPreferences
import javax.inject.Inject

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FavoritesDataSource @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val MOVIE_FAVORITE_LIST_KEY = "fav_movie_ids"
    }

    fun saveIdToFavorites(id: String) {
        val newIds = HashSet<String>(prefs.getStringSet(MOVIE_FAVORITE_LIST_KEY, hashSetOf()))
        newIds.add(id)
        prefs.edit().putStringSet(MOVIE_FAVORITE_LIST_KEY, newIds).apply()
    }

    fun removeIdFromFavorites(id: String) {
        val newIds = HashSet<String>(prefs.getStringSet(MOVIE_FAVORITE_LIST_KEY, hashSetOf()))
        newIds.remove(id)
        prefs.edit().putStringSet(MOVIE_FAVORITE_LIST_KEY, newIds).apply()
    }

    fun getFavoriteIds(): Set<String> =prefs.getStringSet(MOVIE_FAVORITE_LIST_KEY, mutableSetOf())!!
}