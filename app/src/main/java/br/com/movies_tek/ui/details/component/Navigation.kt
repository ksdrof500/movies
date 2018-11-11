package br.com.movies_tek.ui.details.component

import android.content.Intent
import android.net.Uri
import br.com.movies_tek.ui.details.view.DetailsArgs
import br.com.movies_tek.utils.NavigationTarget
import io.reactivex.Observable

const val RS_REMOVE_FROM_FAV = 3
const val RS_DATA_MOVIE_ID = "RS_DATA_MOVIE_ID"
const val YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v="

fun navigationTargets(
        actions: Observable<DetailsAction>,
        detailsArgs: DetailsArgs
): Observable<NavigationTarget> {

    val favDelete = actions
            .ofType(DetailsAction.FavClick::class.java)
            .filter { detailsArgs.fromFavList }
            .map { NavigationTarget.Finish(RS_REMOVE_FROM_FAV, mapOf(RS_DATA_MOVIE_ID to detailsArgs.id)) }

    val videoClick = actions
            .ofType(DetailsAction.VideoClick::class.java)
            .map { "$YOUTUBE_BASE_URL${it.videoViewModel.key}" }
            .map { NavigationTarget.Action(Intent.ACTION_VIEW, Uri.parse(it)) }

    val navigationTargets = listOf(favDelete, videoClick)
    return Observable.merge(navigationTargets)
}