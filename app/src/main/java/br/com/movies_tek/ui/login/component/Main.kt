package br.com.movies_tek.ui.login.component

import br.com.movies_tek.utils.NavigationTarget
import br.com.movies_tek.ui.base.ActivityResult
import br.com.movies_tek.utils.log
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

data class MainResources(
        val uiEvents: MainUiEvents
)

data class MainUiEvents(
        val activityResults: PublishRelay<ActivityResult> = PublishRelay.create(),
        val loginClicks: PublishRelay<ActivityResult> = PublishRelay.create()
)

sealed class MainAction {
    data class LoginAction(val it: GoogleSignInResult) : MainAction()
}

sealed class MainSink {
    data class State(val state: MainAction.LoginAction) : MainSink()
    data class Navigation(val target: NavigationTarget) : MainSink()
}

fun main(
        sources: MainResources,
        initialState: MainState
): Observable<MainSink> = intention(sources)
        .log("action")
        .publish { it ->
            val state = model(initialState, it)?.map { MainSink.State(it) }
            val navigationTargets = navigationTargets(it).map { MainSink.Navigation(it) }
            Observable.merge(state, navigationTargets)
        }
        .share()
