package uz.ttpu.movieshelf.data.mapper

import uz.ttpu.movieshelf.data.remote.MovieDto
import uz.ttpu.movieshelf.domain.model.Movie

//The domain must not know about DTOs
fun MovieDto.toDomain(isFavorite: Boolean): Movie = Movie(
    id = id,
    title = title,
    year = releaseYear,
    rating = score,
    isFavorite = isFavorite
)