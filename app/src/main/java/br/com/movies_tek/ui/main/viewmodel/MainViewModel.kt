package br.com.movies_tek.ui.main.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.ViewModel
import br.com.movies_tek.utils.NavigationTarget
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.data.SharedPrefs
import br.com.movies_tek.ui.main.Sort
import br.com.movies_tek.ui.main.component.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

class MainViewModel(
        sharedPrefs: SharedPrefs,
        movieStorage: MovieStorage,
        sortOptions: List<Sort>
) : ViewModel() {

    val state: LiveData<MainState>
    val navigation: Observable<NavigationTarget>

    val uiEvents = MainUiEvents()

    init {
        val sources = MainResources(uiEvents, sharedPrefs, movieStorage)
        val initialState = MainState(sortOptions[0])
        val sinks = main(sources, initialState, sortOptions)

        state = LiveDataReactiveStreams.fromPublisher(sinks
                .ofType(MainSink.State::class.java)
                .map { it.state }
                .toFlowable(BackpressureStrategy.LATEST)
        )
        navigation = sinks
                .ofType(MainSink.Navigation::class.java)
                .map { it.target }
    }
}