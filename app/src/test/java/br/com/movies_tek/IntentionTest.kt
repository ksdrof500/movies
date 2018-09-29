package br.com.movies_tek.ui.grid.component

import android.content.Intent
import br.com.movies_tek.ImmediateSchedulersRule
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.data.SharedPrefs
import br.com.movies_tek.data.db.MovieDb
import br.com.movies_tek.data.service.TheMovieDbService
import br.com.movies_tek.ui.base.ActivityResult
import br.com.movies_tek.ui.details.component.RS_DATA_MOVIE_ID
import br.com.movies_tek.ui.details.component.RS_REMOVE_FROM_FAV
import br.com.movies_tek.ui.main.component.*
import br.com.movies_tek.ui.main.makeSortOptions
import br.com.movies_tek.ui.main.view.SelectedMovie
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class IntentionTest {

    val sharedPrefs: SharedPrefs = Mockito.mock(SharedPrefs::class.java)
    val movieStorage = MovieStorage(Mockito.mock(TheMovieDbService::class.java), Mockito.mock(MovieDb::class.java))

    val sortOptions = makeSortOptions { "someRandomTitle" }
    val uiEvents = MainUiEvents()
    val sources = MainResources(uiEvents, sharedPrefs, movieStorage)

    @Suppress("unused")
    @get:Rule
    val immediateSchedulersRule = ImmediateSchedulersRule()

    @Before
    fun setUp() {
        Mockito.`when`(sharedPrefs.getSortPos()).thenReturn(Observable.just(0))
    }

    @Test
    fun shouldMapSortSelectionToSelectionWithPrevious() {
        val intentions = intention(sources, sortOptions)
        val observer = intentions.test()

        val expectedActions = listOf(
                MainAction.SortSelection(sortOptions[0], sortOptions[0]),
                MainAction.SortSelection(sortOptions[1], sortOptions[0]),
                MainAction.SortSelection(sortOptions[2], sortOptions[1])
        )

        uiEvents.sortSelections.accept(0) // imitate spinner behaviour, emit 0 emission even if nothing is selected yet
        uiEvents.sortSelections.accept(0)
        uiEvents.sortSelections.accept(0) // same value emission should be ignored
        uiEvents.sortSelections.accept(1)
        uiEvents.sortSelections.accept(2)

        observer.assertValues(*expectedActions.toTypedArray())
        observer.assertNoErrors()
        observer.assertNotComplete()
    }

    @Test
    fun shouldStartWithSortFromSavedPrefs() {
        Mockito.`when`(sharedPrefs.getSortPos()).thenReturn(Observable.just(1))

        val intentions = intention(sources, sortOptions)
        val observer = intentions.test()

        val expectedActions = listOf(
                MainAction.SortSelection(sortOptions[1], sortOptions[0])
        )

        observer.assertValues(*expectedActions.toTypedArray())
        observer.assertNoErrors()
        observer.assertNotComplete()
    }

    @Test
    fun shouldMapMovieClick() {
        val intentions = intention(sources, sortOptions)
        val observer = intentions.test()

        val selectedMovie = SelectedMovie(0, "title", "releaseData", "overview", 12.5, "poster", "backdrop", null)
        val expectedActions = listOf(
                MainAction.SortSelection(sortOptions[0], sortOptions[0]),
                MainAction.MovieClick(selectedMovie)
        )

        uiEvents.movieClicks.accept(selectedMovie)

        observer.assertValues(*expectedActions.toTypedArray())
        observer.assertNoErrors()
        observer.assertNotComplete()
    }

    @Test
    fun shouldMapScrollToCorrectNextPage() {
        val intentions = intention(sources, sortOptions)
        val observer = intentions.test()

        val expectedActions = listOf(
                MainAction.SortSelection(sortOptions[0], sortOptions[0]),
                MainAction.LoadMore(2),
                MainAction.LoadMore(3),
                MainAction.LoadMore(4)
        )

        uiEvents.loadMore.accept(Unit)
        uiEvents.loadMore.accept(Unit)
        uiEvents.loadMore.accept(Unit)

        observer.assertValues(*expectedActions.toTypedArray())
        observer.assertNoErrors()
        observer.assertNotComplete()
    }

    @Test
    fun shouldMapRefreshSwipe() {
        val intentions = intention(sources, sortOptions)
        val observer = intentions.test()

        val expectedActions = listOf(
                MainAction.SortSelection(sortOptions[0], sortOptions[0]),
                MainAction.RefreshSwipe
        )

        uiEvents.refreshSwipes.accept(Unit)

        observer.assertValues(*expectedActions.toTypedArray())
        observer.assertNoErrors()
        observer.assertNotComplete()
    }

    @Test
    fun shouldMapActivityResultsCorrectly() {
        val intent = Mockito.mock(Intent::class.java)
        Mockito.`when`(intent.putExtra(RS_DATA_MOVIE_ID, 0)).thenReturn(intent)
        Mockito.`when`(intent.getIntExtra(RS_DATA_MOVIE_ID, -1)).thenReturn(0)

        val intentions = intention(sources, sortOptions)
        val observer = intentions.test()

        val expectedActions = listOf(
                MainAction.SortSelection(sortOptions[0], sortOptions[0]),
                MainAction.FavDelete(0)
        )

        uiEvents.activityResults.accept(ActivityResult(RQ_DETAILS, RS_REMOVE_FROM_FAV, intent))
        uiEvents.activityResults.accept(
                ActivityResult(RQ_DETAILS, RS_REMOVE_FROM_FAV, null)) // data is null, should be ignored
        uiEvents.activityResults.accept(
                ActivityResult(0, RS_REMOVE_FROM_FAV, intent)) // other reqCode, should be ignored
        uiEvents.activityResults.accept(ActivityResult(RQ_DETAILS, 2, intent)) // other resCode, should be ignored
        Mockito.`when`(intent.getIntExtra(RS_DATA_MOVIE_ID, -1)).thenReturn(-1)
        uiEvents.activityResults.accept(
                ActivityResult(RQ_DETAILS, RS_REMOVE_FROM_FAV, intent)) // intent returns -1, should be ignored

        observer.assertValues(*expectedActions.toTypedArray())
        observer.assertNoErrors()
        observer.assertNotComplete()
    }
}