package br.com.movies_tek.ui.details.component

import br.com.movies_tek.utils.NavigationTarget
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.ui.details.vdos.rows.DetailsVideoRowViewData
import br.com.movies_tek.ui.details.view.DetailsArgs
import br.com.movies_tek.utils.log
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

data class DetailsSources(
        val uiEvents: DetailsUiEvents,
        val movieStorage: MovieStorage
)

data class DetailsUiEvents(
        val snackbarShown: PublishRelay<Unit> = PublishRelay.create(),
        val updateSwipes: PublishRelay<Unit> = PublishRelay.create(),
        val favClicks: PublishRelay<Unit> = PublishRelay.create(),
        val videoClicks: PublishRelay<DetailsVideoRowViewData> = PublishRelay.create()
)

sealed class DetailsAction {
    object SnackbarShown : DetailsAction()
    object UpdateSwipe : DetailsAction()
    object FavClick : DetailsAction()
    data class VideoClick(val videoViewModel: DetailsVideoRowViewData) : DetailsAction()
}

sealed class DetailsSink {
    data class State(val state: DetailsState) : DetailsSink()
    data class Navigation(val target: NavigationTarget) : DetailsSink()
}

fun main(sources: DetailsSources, detailsArgs: DetailsArgs): Observable<DetailsSink> = intention(sources)
        .log("action")
        .publish { it ->
            val state = model(it, sources.movieStorage, detailsArgs)
                    .map { DetailsSink.State(it) }
            val navigationTargets = navigationTargets(it, detailsArgs)
                    .map { DetailsSink.Navigation(it) }

            Observable.merge(state, navigationTargets)
        }
        .share()
