package uz.ttpu.movieshelf.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase
import java.io.IOException

sealed interface MovieListUiState {
    data object Loading : MovieListUiState
    data class Success(
        val movies: List<Movie>,
        val isFromCache: Boolean,
        val favoritesOnly: Boolean = false,
    ) : MovieListUiState
    data class Error(val message: String) : MovieListUiState
}

class MovieListViewModel(
    private val getMovies: GetMoviesUseCase,
    private val getFavoriteMovies: GetFavoriteMoviesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val state: StateFlow<MovieListUiState> = _state.asStateFlow()

    private var isFavoritesOnly = false

    init { load() }

    fun onRefresh() = load()


    fun onFavoritesOnlyChange(enabled: Boolean) {
        isFavoritesOnly = enabled
        load()
    }

    fun onFavoriteClick(movieId: Int) {
        android.util.Log.d("CleanFlow", "Presentation: ViewModel called")
        viewModelScope.launch {
            val newStatus = toggleFavorite(movieId)
            val currentState = _state.value
            if (currentState is MovieListUiState.Success) {
                val updatedMovies = currentState.movies.map { movie ->
                    if (movie.id == movieId) movie.copy(isFavorite = newStatus) else movie
                }


                val finalMovies = if (isFavoritesOnly) {
                    updatedMovies.filter { it.isFavorite }
                } else {
                    updatedMovies
                }

                _state.value = currentState.copy(movies = finalMovies)
            }
        }
    }

    private fun load() {
        _state.value = MovieListUiState.Loading
        viewModelScope.launch {
            try {
                val result = if (isFavoritesOnly) getFavoriteMovies() else getMovies()
                _state.value = MovieListUiState.Success(result.movies, result.isFromCache, isFavoritesOnly)
            } catch (e: IOException) {
                _state.value = MovieListUiState.Error("Can't load movies. Check your connection and try again.")
            }
        }
    }
}