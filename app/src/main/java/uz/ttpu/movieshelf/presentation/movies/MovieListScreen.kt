package uz.ttpu.movieshelf.presentation.movies

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.movieshelf.domain.model.Movie

@Composable
fun MovieListScreen(
    state: MovieListUiState,
    onFavoriteClick: (Int) -> Unit,
    onRefresh: () -> Unit,

    onFavoritesOnlyChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            is MovieListUiState.Loading -> {
                CircularProgressIndicator()
            }
            is MovieListUiState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRefresh) {
                        Text("Retry")
                    }
                }
            }
            is MovieListUiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onRefresh) {
                            Text("Refresh")
                        }
                        FilterChip(
                            selected = state.favoritesOnly,
                            onClick = { onFavoritesOnlyChange(!state.favoritesOnly) },
                            label = { Text("Favorites only") }
                        )
                    }

                    if (state.isFromCache) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Offline — showing saved data",
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = state.movies, key = { it.id }) { movie ->
                            MovieItem(
                                movie = movie,
                                onFavoriteClick = { onFavoriteClick(movie.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onFavoriteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = movie.title, fontWeight = FontWeight.Bold)
            Text(text = "${movie.year} · Rating ${movie.rating}")
        }
        IconButton(onClick = {
            android.util.Log.d("CleanFlow", "UI: Heart clicked")
            onFavoriteClick()
        }) {
            Text(text = if (movie.isFavorite) "❤️" else "🤍")
        }
    }
}

