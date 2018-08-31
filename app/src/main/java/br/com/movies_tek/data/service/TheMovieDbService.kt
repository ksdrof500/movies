package br.com.movies_tek.data.service

import br.com.movies_tek.BuildConfig
import br.com.movies_tek.model.MovieInfo
import br.com.movies_tek.model.MoviesPage
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDbService {
    /**
     * Queries TheMovieDB for movies based on the page number and sort value.
     *
     * @param page   the page to query
     * @param sortBy the value to sort movies by
     * @param apiKey the api key for querying TheMovieDB.
     * @return a [Single] with the query
     */
    @GET("discover/movie")
    fun loadMovies(
            @Query("page") page: Int,
            @Query("sort_by") sortBy: String,
            @Query("api_key") apiKey: String = BuildConfig.MOVIE_DB_API_KEY
    ): Single<MoviesPage>

    /**
     * Queries TheMovieDB for movie details.
     *
     * @param movieId  the db id of the movie
     * @param apiKey   the api key for querying TheMovieDB.
     * @param appendTo the extra query information to append
     * @return a [Single] with the query
     */
    @GET("movie/{id}")
    fun loadMovieInfo(
            @Path("id") movieId: Int,
            @Query("append_to_response") appendTo: String = "reviews,videos",
            @Query("api_key") apiKey: String = BuildConfig.MOVIE_DB_API_KEY
    ): Single<MovieInfo>
}
