package br.com.movies_tek.ui.details.component

import br.com.movies_tek.R
import br.com.movies_tek.data.GetMovieDetailsResult
import br.com.movies_tek.data.LocalDbWriteResult
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.ui.common.SnackbarMessage
import br.com.movies_tek.ui.details.vdos.rows.*
import br.com.movies_tek.ui.details.view.DetailsArgs
import br.com.movies_tek.utils.formatLong
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

data class DetailsState(
        val updating: Boolean = false,
        val title: String = "",
        val backdrop: String? = "",
        val favoured: Boolean = false,
        val details: List<DetailsRowViewData> = emptyList(),
        val snackbar: SnackbarMessage = SnackbarMessage(false)
)

typealias DetailsStateReducer = (DetailsState) -> DetailsState

fun model(
        actions: Observable<DetailsAction>,
        movieStorage: MovieStorage,
        detailsArgs: DetailsArgs
): Observable<DetailsState> {

    val snackbarShown = actions
            .ofType(DetailsAction.SnackbarShown::class.java)
            .map { snackbarReducer() }

    val updateSwipes = actions
            .ofType(DetailsAction.UpdateSwipe::class.java)
            .map { updateSwipeReducer() }

    val args = Observable.just(detailsArgs)
            .map(::argsReducer)

    val getMovieDetails = movieStorage.getMovieDetails(
            detailsArgs.id, detailsArgs.fromFavList).share()

    val movieDetails = getMovieDetails
            .map(::movieDetailsReducer)

    val favClicksWithDetails = actions
            .ofType(DetailsAction.FavClick::class.java)
            .filter { !detailsArgs.fromFavList }
            .withLatestFrom(getMovieDetails,
                    BiFunction<DetailsAction, GetMovieDetailsResult, GetMovieDetailsResult> { _, result -> result })
            .ofType(GetMovieDetailsResult.Success::class.java)
            .map { it.movieDetails }

    val favSave = favClicksWithDetails
            .filter { !it.isFav }
            .flatMap { movieStorage.saveMovieAsFav(it) }
            .map(::saveAsFavReducer)

    val favDelete = favClicksWithDetails
            .filter { it.isFav && !detailsArgs.fromFavList }
            .flatMap { movieStorage.deleteMovieFromFav(it.id) }
            .map(::deleteFromFavReducer)

    val favUpdate = actions
            .ofType(DetailsAction.UpdateSwipe::class.java)
            .flatMap {
                movieStorage.updateFavMovie(detailsArgs.id)
                        .map(::updateFavReducer)
                        .startWith(updateSwipeReducer())
            }

    val initialState = DetailsState()

    val reducers = listOf(snackbarShown, args, movieDetails, updateSwipes, favSave, favDelete, favUpdate)
    return Observable.merge(reducers)
            .scan(initialState) { state, reducer -> reducer(state) }
            .skip(1) // skip initial scan emission
            .distinctUntilChanged()
}

private fun snackbarReducer(): DetailsStateReducer = {
    it.copy(snackbar = it.snackbar.copy(show = false))
}

private fun argsReducer(args: DetailsArgs): DetailsStateReducer = {
    val details = listOf(
            DetailsInfoRowViewData(
                    args.poster,
                    args.releaseDate,
                    args.voteAverage,
                    args.overview
            ),
            DetailsLoadingRowViewData()
    )
    it.copy(
            title = args.title,
            backdrop = args.backdrop,
            details = details
    )
}

private fun movieDetailsReducer(result: GetMovieDetailsResult): DetailsStateReducer = { it ->
    when (result) {
        is GetMovieDetailsResult.Failure -> it.copy(
                snackbar = SnackbarMessage(true, R.string.snackbar_movie_load_reviews_videos_failed),
                details = it.details.minus(it.details.last())
        )
        is GetMovieDetailsResult.Success -> {
            val movieDetails = result.movieDetails
            val details = listOf<DetailsRowViewData>(DetailsInfoRowViewData(
                    movieDetails.poster,
                    movieDetails.releaseDate.formatLong(),
                    movieDetails.voteAverage,
                    movieDetails.overview
            )).let { it ->
                if (movieDetails.videos.isNotEmpty()) {
                    it
                            .plus(DetailsHeaderRowViewData(R.string.header_trailers))
                            .plus(movieDetails.videos.map {
                                DetailsVideoRowViewData(it.key, it.name, it.site, it.size)
                            })
                } else it
            }.let { it ->
                if (movieDetails.reviews.isNotEmpty()) {
                    it
                            .plus(DetailsHeaderRowViewData(R.string.header_reviews))
                            .plus(movieDetails.reviews.map {
                                DetailsReviewRowViewData(it.author, it.content)
                            })
                } else it
            }
            it.copy(
                    title = movieDetails.title,
                    backdrop = movieDetails.backdrop,
                    details = details,
                    favoured = movieDetails.isFav

            )
        }
    }
}

private fun updateSwipeReducer(): DetailsStateReducer = { it.copy(updating = true) }

private fun saveAsFavReducer(result: LocalDbWriteResult.SaveAsFav): DetailsStateReducer = {
    val snackbar = SnackbarMessage(true,
            if (result.successful) R.string.snackbar_movie_added_to_favorites
            else R.string.snackbar_movie_insert_failed)
    it.copy(snackbar = snackbar)
}

private fun deleteFromFavReducer(result: LocalDbWriteResult.DeleteFromFav): DetailsStateReducer = {
    val snackbar = SnackbarMessage(true,
            if (result.successful) R.string.snackbar_movie_removed_from_favorites
            else R.string.snackbar_movie_delete_failed)
    it.copy(snackbar = snackbar)
}

private fun updateFavReducer(result: LocalDbWriteResult.UpdateFav): DetailsStateReducer = {
    val snackbar = SnackbarMessage(true,
            if (result.successful) R.string.snackbar_movie_updated
            else R.string.snackbar_movie_update_failed)
    it.copy(updating = false, snackbar = snackbar)
}
