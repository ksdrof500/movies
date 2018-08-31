package br.com.movies_tek.ui.main.component

import br.com.movies_tek.utils.NavigationTarget
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.data.SharedPrefs
import br.com.movies_tek.ui.base.ActivityResult
import br.com.movies_tek.ui.main.Sort
import br.com.movies_tek.ui.main.view.SelectedMovie
import br.com.movies_tek.utils.log
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

data class MainResources(
        val uiEvents: MainUiEvents,
        val sharedPrefs: SharedPrefs,
        val movieStorage: MovieStorage
)

data class MainUiEvents(
        val activityResults: PublishRelay<ActivityResult> = PublishRelay.create(),
        val snackbarShown: PublishRelay<Unit> = PublishRelay.create(),
        val sortSelections: PublishRelay<Int> = PublishRelay.create(),
        val movieClicks: PublishRelay<SelectedMovie> = PublishRelay.create(),
        val loadMore: PublishRelay<Unit> = PublishRelay.create(),
        val refreshSwipes: PublishRelay<Unit> = PublishRelay.create()
)

sealed class MainAction {
    object SnackbarShown : MainAction()
    data class SortSelection(val sort: Sort, val sortPrev: Sort) : MainAction()
    data class MovieClick(val selectedMovie: SelectedMovie) : MainAction()
    data class LoadMore(val page: Int) : MainAction()
    object RefreshSwipe : MainAction()
    data class FavDelete(val movieId: Int) : MainAction()
}

sealed class MainSink {
    data class State(val state: MainState) : MainSink()
    data class Navigation(val target: NavigationTarget) : MainSink()
}

fun main(
        sources: MainResources,
        initialState: MainState,
        sortOptions: List<Sort>
): Observable<MainSink> = intention(sources, sortOptions)
        .log("action")
        .publish { it ->
            val state = model(sortOptions, initialState, it, sources.movieStorage, sources.sharedPrefs)
                    .map { MainSink.State(it) }
            val navigationTargets = navigationTargets(it)
                    .map { MainSink.Navigation(it) }

            Observable.merge(state, navigationTargets)
        }
        .share()
