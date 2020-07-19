package net.dejanjokic.arsmovies.data

import net.dejanjokic.arsmovies.data.db.MovieDetailsEntity
import net.dejanjokic.arsmovies.data.db.MovieListItemEntity
import net.dejanjokic.arsmovies.data.domain.DomainDetails
import net.dejanjokic.arsmovies.data.domain.DomainItem
import net.dejanjokic.arsmovies.data.network.MovieDetailsResponse
import net.dejanjokic.arsmovies.data.network.MovieDto

object MovieMapper {

    private fun movieListItemEntityToDomain(entity: MovieListItemEntity): DomainItem.Movie =
        DomainItem.Movie(
            imdbId = entity.imdbId,
            title = entity.title,
            year = entity.year,
            poster = entity.posterUrl,
            isFavorite = entity.isFavorite
        )

    fun movieDetailsEntityToDomain(entity: MovieDetailsEntity): DomainDetails =
        DomainDetails(
            imdbId = entity.imdbId,
            title = entity.title,
            year = entity.year,
            rated = entity.rated,
            releaseDate = entity.releaseDate,
            runtime = entity.runtime,
            genres = entity.genres,
            director = entity.director,
            writer = entity.writer,
            actors = entity.actors,
            plot = entity.plot,
            language = entity.language,
            country = entity.country,
            awards = entity.awards,
            posterUrl = entity.posterUrl,
            metaScore = entity.metaScore,
            imdbRating = entity.imdbRating,
            imdbVotes = entity.imdbVotes,
            boxOffice = entity.boxOffice,
            production = entity.production,
            website = entity.website,
            isFavorite = entity.isFavorite
        )

    fun entityListToDomain(entities: List<MovieListItemEntity>): List<DomainItem> {
        val items = mutableListOf<DomainItem.Movie>()
        entities.forEach {
            items.add(movieListItemEntityToDomain(it))
        }
        val sortedGroupedMovies = items.sortedByDescending { it.year }.groupBy { it.year }
        val finalData = mutableListOf<DomainItem>()
        sortedGroupedMovies.forEach { (year, items) ->
            finalData.add(DomainItem.Header(year))
            items.forEach {
                finalData.add(it)
            }
        }
        return finalData
    }

    fun movieDetailsResponseToEntity(response: MovieDetailsResponse) = MovieDetailsEntity(
        imdbId = response.imdbId,
        title = response.title,
        year = response.year,
        rated = response.rated,
        releaseDate = response.releaseDate,
        runtime = response.runtime,
        genres = response.genres,
        director = response.director,
        writer = response.writer,
        actors = response.actors,
        plot = response.plot,
        language = response.language,
        country = response.country,
        awards = response.awards,
        posterUrl = response.posterUrl,
        metaScore = response.metaScore,
        imdbRating = response.imdbRating,
        imdbVotes = response.imdbVotes,
        boxOffice = response.boxOffice,
        production = response.production,
        website = response.website
    )

    fun movieSearchResponseToDomain(items: List<MovieDto>): List<DomainItem> {
        val sortedGroupedMovies = items.sortedByDescending { it.year }.groupBy { it.year }
        val finalData = mutableListOf<DomainItem>()
        sortedGroupedMovies.forEach { (year, items) ->
            finalData.add(DomainItem.Header(year))
            items.forEach {
                finalData.add(DomainItem.Movie(it.imdbId, it.title, it.year, it.posterUrl))
            }
        }
        return finalData
    }

    fun movieDetailsResponseToDomain(response: MovieDetailsResponse) = DomainDetails(
        imdbId = response.imdbId,
        title = response.title,
        year = response.year,
        rated = response.rated,
        releaseDate = response.releaseDate,
        runtime = response.runtime,
        genres = response.genres,
        director = response.director,
        writer = response.writer,
        actors = response.actors,
        plot = response.plot,
        language = response.language,
        country = response.country,
        awards = response.awards,
        posterUrl = response.posterUrl,
        metaScore = response.metaScore,
        imdbRating = response.imdbRating,
        imdbVotes = response.imdbVotes,
        boxOffice = response.boxOffice,
        production = response.production,
        website = response.website
    )

    fun movieDomainItemToEntity(item: DomainItem.Movie) = MovieListItemEntity(
        imdbId = item.imdbId,
        title = item.title,
        year = item.year,
        posterUrl = item.poster,
        isFavorite = item.isFavorite
    )

    fun movieDetailsDomainToEntity(item: DomainDetails) = MovieDetailsEntity(
        imdbId = item.imdbId,
        title = item.title,
        year = item.year,
        rated = item.rated,
        releaseDate = item.releaseDate,
        runtime = item.runtime,
        genres = item.genres,
        director = item.director,
        writer = item.writer,
        actors = item.actors,
        plot = item.plot,
        language = item.language,
        country = item.country,
        awards = item.awards,
        posterUrl = item.posterUrl,
        metaScore = item.metaScore,
        imdbRating = item.imdbRating,
        imdbVotes = item.imdbVotes,
        boxOffice = item.boxOffice,
        production = item.production,
        website = item.website,
        isFavorite = item.isFavorite
    )
}