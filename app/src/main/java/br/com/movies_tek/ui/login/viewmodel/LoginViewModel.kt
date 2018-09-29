package br.com.movies_tek.ui.login.viewmodel

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.data.SharedPrefs
import br.com.movies_tek.ui.login.component.*
import br.com.movies_tek.ui.main.MainActivity
import br.com.movies_tek.ui.main.Sort
import br.com.movies_tek.utils.NavigationTarget
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

class LoginViewModel(val firebaseAuth: FirebaseAuth, val loginDelegate: LoginDelegate) : ViewModel() {

    val navigation: Observable<NavigationTarget>

    val uiEvents = MainUiEvents()

    init {
        val sources = MainResources(uiEvents)
        val initialState = MainState()
        val sinks = main(sources, initialState)

        navigation = sinks
                .ofType(MainSink.Navigation::class.java)
                .map { it.target }
    }


    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount, activity: Activity) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        loginDelegate.moveForward(MainActivity())
                    } else {
                        loginDelegate.moveForward(MainActivity())
                    }
                }
    }

    fun loginClick() {
        loginDelegate.login()
    }

}