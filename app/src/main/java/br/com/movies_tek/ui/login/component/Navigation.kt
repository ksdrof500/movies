package br.com.movies_tek.ui.login.component

import android.content.Intent
import android.net.Uri
import br.com.movies_tek.utils.NavigationTarget
import br.com.movies_tek.R
import br.com.movies_tek.ui.details.component.DetailsAction
import br.com.movies_tek.ui.details.component.YOUTUBE_BASE_URL
import br.com.movies_tek.ui.details.view.DetailsActivity
import br.com.movies_tek.ui.details.view.DetailsArgs
import br.com.movies_tek.ui.main.MainActivity
import br.com.movies_tek.ui.main.SortOption
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

const val RC_SIGN_IN = 9001

fun navigationTargets(actions: Observable<MainAction>): Observable<NavigationTarget> {

    val loginClicks = actions
            .ofType(MainAction.LoginAction::class.java)
            .map { NavigationTarget.Activity(MainActivity::class.java, null, RC_SIGN_IN, null, null) }

    val navigationTargets = listOf(loginClicks)
    return Observable.merge(navigationTargets)
}
