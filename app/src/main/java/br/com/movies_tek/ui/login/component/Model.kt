package br.com.movies_tek.ui.login.component

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

typealias MainStateReducer = (MainState) -> MainState

data class MainState(
        val loading: Boolean = false
)

fun model(
        initialState: MainState,
        actions: Observable<MainAction>
): Observable<MainAction.LoginAction>? {

    val login = actions
            .ofType(MainAction.LoginAction::class.java)

    val reducers = listOf(login)
    return Observable.merge(reducers)
            .skip(1) // skip initial scan emission
            .distinctUntilChanged()
}






