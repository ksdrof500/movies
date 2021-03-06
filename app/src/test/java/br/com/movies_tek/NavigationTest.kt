package br.com.movies_tek

import br.com.movies_tek.ui.details.view.DetailsActivity
import br.com.movies_tek.ui.details.view.DetailsArgs
import br.com.movies_tek.ui.main.component.MainAction
import br.com.movies_tek.ui.main.component.RQ_DETAILS
import br.com.movies_tek.ui.main.component.navigationTargets
import br.com.movies_tek.ui.main.makeSortOptions
import br.com.movies_tek.ui.main.view.SelectedMovie
import br.com.movies_tek.utils.NavigationTarget
import com.jakewharton.rxrelay2.PublishRelay
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    private val sortOptions = makeSortOptions { "someRandomTitle" }
    private val actions: PublishRelay<MainAction> = PublishRelay.create()

    @Suppress("unused")
    @get:Rule
    val immediateSchedulersRule = ImmediateSchedulersRule()

    @Test
    fun shouldNavigateToToDetailsScreenOnMovieClick() {
        val navigation = navigationTargets(actions)
        val observer = navigation.test()

        val sortPop = sortOptions[0]
        val sortFav = sortOptions[2]
        val selectedMovie = SelectedMovie(0, "title", "releaseData", "overview", 12.5, "poster", "backdrop", null)

        val expectedNavigationTargets = listOf(
                NavigationTarget.Activity(DetailsActivity::class.java,
                        DetailsArgs(selectedMovie.id, selectedMovie.title, selectedMovie.releaseDate,
                                selectedMovie.overview, selectedMovie.voteAverage, selectedMovie.poster,
                                selectedMovie.backdrop, false),
                        RQ_DETAILS,
                        selectedMovie.posterView,
                        R.string.shared_transition_details_poster
                ),
                NavigationTarget.Activity(DetailsActivity::class.java,
                        DetailsArgs(selectedMovie.id, selectedMovie.title, selectedMovie.releaseDate,
                                selectedMovie.overview, selectedMovie.voteAverage, selectedMovie.poster,
                                selectedMovie.backdrop, true),
                        RQ_DETAILS,
                        selectedMovie.posterView,
                        R.string.shared_transition_details_poster
                )
        )

        actions.accept(MainAction.SortSelection(sortPop, sortPop))
        actions.accept(MainAction.MovieClick(selectedMovie))
        actions.accept(MainAction.SortSelection(sortFav, sortPop))
        actions.accept(MainAction.MovieClick(selectedMovie))

        observer.assertValues(*expectedNavigationTargets.toTypedArray())
        observer.assertNoErrors()
        observer.assertNotComplete()
    }
}