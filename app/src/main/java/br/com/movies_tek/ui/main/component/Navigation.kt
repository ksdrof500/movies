package br.com.movies_tek.ui.main.component

import br.com.movies_tek.R
import br.com.movies_tek.ui.details.view.DetailsActivity
import br.com.movies_tek.ui.details.view.DetailsArgs
import br.com.movies_tek.ui.main.SortOption
import br.com.movies_tek.utils.NavigationTarget
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

const val RQ_DETAILS = 1

fun navigationTargets(actions: Observable<MainAction>): Observable<NavigationTarget> {
    val sortSelections = actions
            .ofType(MainAction.SortSelection::class.java)

    val movieClicks = actions
            .ofType(MainAction.MovieClick::class.java)
            .withLatestFrom(sortSelections,
                    BiFunction<MainAction.MovieClick, MainAction.SortSelection, NavigationTarget>
                    { (selectedMovie), (sort) ->
                        val args = DetailsArgs(selectedMovie.id, selectedMovie.title, selectedMovie.releaseDate,
                                selectedMovie.overview, selectedMovie.voteAverage, selectedMovie.poster,
                                selectedMovie.backdrop, sort.option == SortOption.SORT_FAVORITE)
                        NavigationTarget.Activity(DetailsActivity::class.java, args, RQ_DETAILS,
                                selectedMovie.posterView, R.string.shared_transition_details_poster)
                    })

    val navigationTargets = listOf(movieClicks)
    return Observable.merge(navigationTargets)
}
