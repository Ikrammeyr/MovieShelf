package uz.ttpu.movieshelf.data.local

import android.content.Context
import uz.ttpu.movieshelf.data.remote.MovieDto

class SharedPrefsMovieLocalDataSource(context: Context) : MovieLocalDataSource {

    private val prefs = context.getSharedPreferences("movieshelf", Context.MODE_PRIVATE)
    private var cached: List<MovieDto>? = null

    override suspend fun getCachedMovies(): List<MovieDto>? = cached

    override suspend fun saveMovies(movies: List<MovieDto>) {
        cached = movies
    }

    override suspend fun getFavoriteIds(): Set<Int> {
        val stringSet = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
        return stringSet.map { it.toInt() }.toSet()
    }

    override suspend fun setFavorite(id: Int, favorite: Boolean) {
        android.util.Log.d("CleanFlow", "Data: SharedPrefsMovieLocalDataSource.setFavorite")
        val stringSet = prefs.getStringSet("favorites", emptySet()) ?: emptySet()

        val mutableSet = stringSet.toMutableSet()
        if (favorite) {
            mutableSet.add(id.toString())
        } else {
            mutableSet.remove(id.toString())
        }
        prefs.edit().putStringSet("favorites", mutableSet).apply()
    }
}