package br.com.movies_tek.ui.login.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.data.SharedPrefs
import br.com.movies_tek.ui.login.component.LoginDelegate
import br.com.movies_tek.ui.main.Sort
import br.com.movies_tek.ui.main.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModelFactory(val firebaseAuth: FirebaseAuth, val loginDelegate: LoginDelegate) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LoginViewModel(firebaseAuth, loginDelegate) as T
    }
}