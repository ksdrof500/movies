package br.com.movies_tek.ui.details.component

import br.com.movies_tek.model.YOU_TUBE
import io.reactivex.Observable

fun intention(sources: DetailsSources): Observable<DetailsAction> {
    val snackbarShown = sources.uiEvents.snackbarShown
            .map { DetailsAction.SnackbarShown }

    val favClicks = sources.uiEvents.favClicks
            .map { DetailsAction.FavClick }

    val updateSwipes = sources.uiEvents.updateSwipes
            .map { DetailsAction.UpdateSwipe }

    val videoClicks = sources.uiEvents.videoClicks
            .filter { it.site == YOU_TUBE }
            .map { DetailsAction.VideoClick(it) }

    val actions = listOf(snackbarShown, favClicks, updateSwipes, videoClicks)

    return Observable.merge(actions)
}