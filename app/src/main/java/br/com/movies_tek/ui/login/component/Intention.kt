package br.com.movies_tek.ui.login.component

import com.google.android.gms.auth.api.Auth
import io.reactivex.Observable

fun intention(
        sources: MainResources
): Observable<MainAction> {

    val loginClicks = sources.uiEvents.activityResults
            .filter { it.requestCode == RC_SIGN_IN }
            .map {
                Auth.GoogleSignInApi.getSignInResultFromIntent(it.data)
            }
            .map { MainAction.LoginAction(it) }

    val actions = listOf(loginClicks)
    return Observable.merge(actions)
}