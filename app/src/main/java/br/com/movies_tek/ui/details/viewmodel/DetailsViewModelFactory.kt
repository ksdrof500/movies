package br.com.movies_tek.ui.details.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.ui.details.view.DetailsArgs

class DetailsViewModelFactory(
        val movieStorage: MovieStorage,
        private val detailsArgs: DetailsArgs
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DetailsViewModel(movieStorage, detailsArgs) as T
    }
}