package br.com.movies_tek.ui.main.component

import br.com.movies_tek.R
import br.com.movies_tek.data.GetMoviesResult
import br.com.movies_tek.data.LocalDbWriteResult
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.data.SharedPrefs
import br.com.movies_tek.ui.common.SnackbarMessage
import br.com.movies_tek.ui.main.Sort
import br.com.movies_tek.ui.main.SortOption
import br.com.movies_tek.ui.main.vdos.rows.MainRowLoadMoreViewData
import br.com.movies_tek.ui.main.vdos.rows.MainRowMovieViewData
import br.com.movies_tek.ui.main.vdos.rows.MainRowViewData
import br.com.movies_tek.utils.formatLong
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

data class MainState(
        val sort: Sort,
        val movies: List<MainRowViewData> = emptyList(),
        val empty: Boolean = false,
        val loading: Boolean = false,
        val loadingMore: Boolean = false,
        val refreshing: Boolean = false,
        val snackbar: SnackbarMessage = SnackbarMessage(false)
)

typealias MainStateReducer = (MainState) -> MainState

data class PageWithSort(val page: Int, val sort: Sort)

fun model(
        sortOptions: List<Sort>,
        initialState: MainState,
        actions: Observable<MainAction>,
        movieStorage: MovieStorage,
        sharedPrefs: SharedPrefs
): Observable<MainState> {
    val snackbars = actions
            .ofType(MainAction.SnackbarShown::class.java)
            .map { snackbarReducer() }

    val sortSelectionActions = actions
            .ofType(MainAction.SortSelection::class.java)

    val sortSelections = sortSelectionActions
            .switchMap {
                if (it.sort.option == SortOption.SORT_FAVORITE) {
                    movieStorage.getFavMovies()
                            .map(::moviesReducer)
                } else {
                    movieStorage.getOnlMovies(1, it.sort.option, false)
                            .map(::moviesReducer)
                }.startWith(sortSelectionsReducer(it))
            }

    val sortSelectionSave = sortSelectionActions
            .map { sortOptions.indexOf(it.sort) }
            .flatMap { sharedPrefs.writeSortPos(it) }
            .map { sortSelectionSaveReducer() }

    val pageWithSort = actions
            .ofType(MainAction.LoadMore::class.java)
            .withLatestFrom(sortSelectionActions,
                    BiFunction<MainAction.LoadMore, MainAction.SortSelection, PageWithSort>
                    { (page), (sort) -> PageWithSort(page, sort) })

    val loadMore = pageWithSort
            .switchMap {
                movieStorage.getOnlMovies(it.page, it.sort.option, false)
                        .map(::moviesOnlMoreReducer)
                        .startWith(loadMoreReducer(MainRowLoadMoreViewData()))
            }

    val refreshSwipes = actions
            .ofType(MainAction.RefreshSwipe::class.java)
            .withLatestFrom(pageWithSort, BiFunction<MainAction, PageWithSort, PageWithSort>
            { _, pageWithSort -> pageWithSort })
            .switchMap {
                movieStorage.getOnlMovies(it.page, it.sort.option, true)
                        .map(::moviesReducer)
                        .startWith(refreshingReducer())
            }

    val favDelete = actions
            .ofType(MainAction.FavDelete::class.java)
            .flatMap { movieStorage.deleteMovieFromFav(it.movieId) }
            .map(::favDeleteReducer)

    val reducers = listOf(snackbars, sortSelections, sortSelectionSave, loadMore, refreshSwipes, favDelete)
    return Observable.merge(reducers)
            .scan(initialState) { state, reducer -> reducer(state) }
            .skip(1) // skip initial scan emission
            .distinctUntilChanged()
}

private fun snackbarReducer(): MainStateReducer = {
    it.copy(snackbar = it.snackbar.copy(show = false))
}

private fun sortSelectionsReducer(sortSelection: MainAction.SortSelection): MainStateReducer = {
    it.copy(sort = sortSelection.sort, loading = true)
}

private fun sortSelectionSaveReducer(): MainStateReducer = { it }

private fun refreshingReducer(): MainStateReducer = { it.copy(refreshing = true) }

private fun loadMoreReducer(loadMoreViewData: MainRowLoadMoreViewData): MainStateReducer = {
    it.copy(loadingMore = true, movies = it.movies.plus(loadMoreViewData))
}

private fun moviesReducer(result: GetMoviesResult): MainStateReducer = { state ->
    when (result) {
        is GetMoviesResult.Failure -> state.copy(
                loading = false,
                refreshing = false,
                movies = emptyList(),
                empty = true,
                snackbar = SnackbarMessage(true, R.string.snackbar_movies_load_failed))
        is GetMoviesResult.Success -> {
            val movies = result.movies
                    .map {
                        MainRowMovieViewData(it.id, it.title, it.overview, it.releaseDate.formatLong(), it.voteAverage,
                                it.poster, it.backdrop, it.voteAverage)
                    }
            state.copy(
                    movies = movies,
                    empty = movies.isEmpty(),
                    loading = false,
                    loadingMore = false,
                    refreshing = false
            )
        }
    }
}

private fun moviesOnlMoreReducer(result: GetMoviesResult): MainStateReducer = { state ->
    when (result) {
        is GetMoviesResult.Failure -> state.copy(
                loadingMore = false,
                movies = state.movies.minus(state.movies.last()),
                snackbar = SnackbarMessage(true, R.string.snackbar_movies_load_failed))
        is GetMoviesResult.Success -> {
            val movies = result.movies
                    .map {
                        MainRowMovieViewData(it.id, it.title, it.overview, it.releaseDate.formatLong(), it.voteAverage,
                                it.poster, it.backdrop, it.voteAverage)
                    }
            state.copy(movies = state.movies.minus(state.movies.last()).plus(movies), loadingMore = false)
        }
    }
}

private fun favDeleteReducer(result: LocalDbWriteResult.DeleteFromFav): MainStateReducer = {
    it.copy(snackbar = SnackbarMessage(true,
            if (result.successful) R.string.snackbar_movie_removed_from_favorites
            else R.string.snackbar_movie_delete_failed))
}
