

package br.com.movies_tek.ui.main.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.data.SharedPrefs
import br.com.movies_tek.ui.main.Sort

class MainViewModelFactory(
        val sharedPrefs: SharedPrefs,
        val movieStorage: MovieStorage,
        val sortOptions: List<Sort>
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(sharedPrefs, movieStorage, sortOptions) as T
    }
}