package uz.ttpu.movieshelf.data.repository

import kotlinx.coroutines.CancellationException
import uz.ttpu.movieshelf.data.local.MovieLocalDataSource
import uz.ttpu.movieshelf.data.mapper.toDomain
import uz.ttpu.movieshelf.data.remote.MovieRemoteDataSource
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import java.io.IOException

class MovieRepositoryImpl(
    private val remote: MovieRemoteDataSource,
    private val local: MovieLocalDataSource,
) : MovieRepository {

    override suspend fun getMovies(): MoviesResult {
        val favoriteIds = local.getFavoriteIds()

        return try {
            val remoteMovies = remote.fetchMovies()
            local.saveMovies(remoteMovies)

            val domainMovies = remoteMovies.map { it.toDomain(favoriteIds.contains(it.id)) }
            MoviesResult(movies = domainMovies, isFromCache = false)

        } catch (e: IOException) {
            val cachedMovies = local.getCachedMovies()
            if (cachedMovies != null) {
                val domainMovies = cachedMovies.map { it.toDomain(favoriteIds.contains(it.id)) }
                MoviesResult(movies = domainMovies, isFromCache = true)
            } else {
                throw e
            }
        }
    }

    override suspend fun toggleFavorite(movieId: Int): Boolean {
        android.util.Log.d("CleanFlow", "Data: MovieRepositoryImpl.toggleFavorite")
        val favoriteIds = local.getFavoriteIds()
        val isNowFavorite = !favoriteIds.contains(movieId)

        local.setFavorite(movieId, isNowFavorite)
        return isNowFavorite
    }
}