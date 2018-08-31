package br.com.movies_tek.ui.main.component

import br.com.movies_tek.ui.details.component.RS_DATA_MOVIE_ID
import br.com.movies_tek.ui.details.component.RS_REMOVE_FROM_FAV
import br.com.movies_tek.ui.main.Sort
import br.com.movies_tek.ui.main.SortSelectionState
import io.reactivex.Observable

fun intention(
        sources: MainResources,
        sortOptions: List<Sort>
): Observable<MainAction> {
    val snackbarShown = sources.uiEvents.snackbarShown
            .map { MainAction.SnackbarShown }

    val sortSelectionsSharedPrefs = sources.sharedPrefs.getSortPos()
            .map { SortSelectionState(sortOptions[it], sortOptions[0]) }

    val sortSelectionsSpinner = sources.uiEvents.sortSelections
            .skip(1) // skip initial position 0 emission (spinner always emit this)
            .map { sortOptions[it] }
            .scan(SortSelectionState(sortOptions[0], sortOptions[0])
            ) { (sort), curr -> SortSelectionState(curr, sort) }
            .skip(1) // skip initial scan emission

    val sortSelections = Observable.merge(sortSelectionsSharedPrefs, sortSelectionsSpinner)
            .distinctUntilChanged()
            .map { MainAction.SortSelection(it.sort, it.sortPrev) }

    val movieClicks = sources.uiEvents.movieClicks
            .map { MainAction.MovieClick(it) }

    val loadMore = sources.uiEvents.loadMore
            .map { 1 }
            .scan(1) { acc, curr -> acc + curr }
            .skip(1) // skip initial scan emission
            .map { MainAction.LoadMore(it) }

    val refreshSwipes = sources.uiEvents.refreshSwipes
            .map { MainAction.RefreshSwipe }

    val favDelete = sources.uiEvents.activityResults
            .filter { it.requestCode == RQ_DETAILS && it.resultCode == RS_REMOVE_FROM_FAV }
            .map {
                when {
                    it.data == null -> -1
                    else -> it.data.getIntExtra(RS_DATA_MOVIE_ID, -1)
                }
            }
            .filter { it > -1 }
            .map { MainAction.FavDelete(it) }

    val actions = listOf(snackbarShown, sortSelections, movieClicks, loadMore, refreshSwipes, favDelete)
    return Observable.merge(actions)
}