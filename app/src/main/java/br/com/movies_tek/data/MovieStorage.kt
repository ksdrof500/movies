package br.com.movies_tek.data

import br.com.movies_tek.data.db.MovieDb
import br.com.movies_tek.data.db.tables.MovieEntity
import br.com.movies_tek.data.db.tables.ReviewEntity
import br.com.movies_tek.data.db.tables.VideoEntity
import br.com.movies_tek.data.service.TheMovieDbService
import br.com.movies_tek.model.*
import br.com.movies_tek.ui.main.SortOption
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

sealed class GetMoviesResult {
    data class Success(val movies: List<Movie>) : GetMoviesResult()
    object Failure : GetMoviesResult()
}

sealed class GetMovieDetailsResult {
    data class Success(val movieDetails: MovieDetails) : GetMovieDetailsResult()
    object Failure : GetMovieDetailsResult()
}

sealed class LocalDbWriteResult {
    data class SaveAsFav(val successful: Boolean) : LocalDbWriteResult()
    data class DeleteFromFav(val successful: Boolean) : LocalDbWriteResult()
    data class UpdateFav(val successful: Boolean) : LocalDbWriteResult()
}

class MovieStorage @Inject constructor(private val theMovieDbService: TheMovieDbService, private val movieDb: MovieDb) {

    fun getFavMovies(): Observable<GetMoviesResult> = movieDb.movieDao().getAll().toObservable()
            .map { it ->
                it.map {
                    Movie(it.id, it.backdrop, it.overview, it.releaseDate, it.poster, it.title, it.voteAverage)
                }
            }
            .map<GetMoviesResult> { GetMoviesResult.Success(it) }
            .onErrorReturn { GetMoviesResult.Failure }

    fun getOnlMovies(page: Int, sortOption: SortOption, fetchAllPages: Boolean): Observable<GetMoviesResult> =
            if (fetchAllPages) {
                Observable.range(1, page)
                        .concatMap { it ->
                            theMovieDbService.loadMovies(it, sortOption.value)
                                    .flattenAsObservable { it.movies }
                        }
                        .toList()

            } else {
                theMovieDbService.loadMovies(page, sortOption.value)
                        .map { it.movies }
            }
                    .toObservable()
                    .map<GetMoviesResult> { GetMoviesResult.Success(it) }
                    .onErrorReturn { GetMoviesResult.Failure }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getMovieDetails(movieId: Int, fromFavList: Boolean): Observable<GetMovieDetailsResult> {
        val movieDetails =
                if (fromFavList) {
                    Flowable.combineLatest(
                            movieDb.movieDao().getById(movieId),
                            movieDb.videoDao().getByMovieId(movieId),
                            movieDb.reviewDao().getByMovieId(movieId),
                            Function3<MovieEntity, List<VideoEntity>, List<ReviewEntity>, MovieDetails>
                            { movie, videos, reviews ->
                                MovieDetails(true, movie.id, movie.title, movie.overview, movie.releaseDate,
                                        movie.voteAverage, movie.poster, movie.backdrop,
                                        videos.map { Video(it.name, it.key, it.site, it.size, it.type) },
                                        reviews.map { Review(it.author, it.content, it.url) })
                            }
                    )
                            .toObservable()
                } else {
                    Observable.combineLatest(
                            theMovieDbService.loadMovieInfo(movieId).toObservable(),
                            movieDb.movieDao().existsById(movieId)
                                    .map { it == 1 }
                                    .toObservable(),
                            BiFunction<MovieInfo, Boolean, MovieDetails> { details, fav ->
                                MovieDetails(fav, details.id, details.title, details.overview, details.releaseDate,
                                        details.voteAverage, details.poster, details.backdrop,
                                        details.videosPage.videos, details.reviewsPage.reviews)
                            }
                    )
                }

        return movieDetails
                .map<GetMovieDetailsResult> { GetMovieDetailsResult.Success(it) }
                .onErrorReturn { GetMovieDetailsResult.Failure }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun saveMovieAsFav(movieDetails: MovieDetails): Observable<LocalDbWriteResult.SaveAsFav> = Observable.fromCallable {
        movieDb.runInTransaction {
            val movieEntity = MovieEntity(movieDetails.id, movieDetails.title, movieDetails.releaseDate,
                    movieDetails.voteAverage, movieDetails.overview, movieDetails.poster, movieDetails.backdrop)
            movieDb.movieDao().insert(movieEntity)

            if (movieDetails.videos.isNotEmpty()) {
                val videoEntities = movieDetails.videos
                        .map { VideoEntity(movieDetails.id, it.name, it.key, it.site, it.size, it.type) }
                movieDb.videoDao().insertAll(videoEntities)
            }

            if (movieDetails.reviews.isNotEmpty()) {
                val reviewEntities = movieDetails.reviews
                        .map { ReviewEntity(movieDetails.id, it.author, it.content, it.url) }
                movieDb.reviewDao().insertAll(reviewEntities)
            }
        }
    }
            .map { LocalDbWriteResult.SaveAsFav(true) }
            .onErrorReturn { LocalDbWriteResult.SaveAsFav(false) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun deleteMovieFromFav(movieId: Int): Observable<LocalDbWriteResult.DeleteFromFav> = Observable.fromCallable {
        movieDb.movieDao().deleteById(movieId)
    }
            .map { LocalDbWriteResult.DeleteFromFav(it > 0) }
            .onErrorReturn { LocalDbWriteResult.DeleteFromFav(false) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun updateFavMovie(movieId: Int): Observable<LocalDbWriteResult.UpdateFav> =
            theMovieDbService.loadMovieInfo(movieId)
                    .toObservable()
                    .flatMap { it ->
                        Observable.fromCallable {
                            movieDb.runInTransaction {
                                val movieEntity = MovieEntity(it.id, it.title, it.releaseDate, it.voteAverage,
                                        it.overview,
                                        it.poster, it.backdrop)
                                movieDb.movieDao().update(movieEntity)
                                movieDb.videoDao().deleteByMovieId(it.id)
                                movieDb.reviewDao().deleteByMovieId(it.id)

                                if (it.videosPage.videos.isNotEmpty()) {
                                    val videoEntities = it.videosPage.videos
                                            .map { VideoEntity(movieId, it.name, it.key, it.site, it.size, it.type) }
                                    movieDb.videoDao().insertAll(videoEntities)
                                }

                                if (it.reviewsPage.reviews.isNotEmpty()) {
                                    val reviewEntities = it.reviewsPage.reviews
                                            .map { ReviewEntity(movieId, it.author, it.content, it.url) }
                                    movieDb.reviewDao().insertAll(reviewEntities)
                                }
                            }
                        }
                    }
                    .map { LocalDbWriteResult.UpdateFav(true) }
                    .onErrorReturn { LocalDbWriteResult.UpdateFav(false) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}