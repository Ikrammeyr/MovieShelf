package uz.ttpu.movieshelf

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase

class GetFavoriteMoviesUseCaseTest {
    @Test
    fun returns_only_favorite_movies() = runBlocking<Unit> {
        val mixedMovies = listOf(
            Movie(1, "A", 2020, 8.0, isFavorite = false),
            Movie(2, "B", 2020, 8.0, isFavorite = true),
            Movie(3, "C", 2020, 9.0, isFavorite = true)
        )
        val fakeResult = MoviesResult(mixedMovies, isFromCache = false)
        val getMoviesUseCase = GetMoviesUseCase(FakeMovieRepository(fakeResult))
        val getFavoriteUseCase = GetFavoriteMoviesUseCase(getMoviesUseCase)

        val result = getFavoriteUseCase()

        assertEquals(2, result.movies.size)
        assertEquals(listOf("B", "C"), result.movies.map { it.title })
    }
}