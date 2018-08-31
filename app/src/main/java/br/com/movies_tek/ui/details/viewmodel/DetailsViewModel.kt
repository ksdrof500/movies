package br.com.movies_tek.ui.details.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.ViewModel
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.ui.details.component.*
import br.com.movies_tek.ui.details.view.DetailsArgs
import br.com.movies_tek.utils.NavigationTarget
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

class DetailsViewModel(
        movieStorage: MovieStorage,
        detailsArgs: DetailsArgs
) : ViewModel() {

    val state: LiveData<DetailsState>
    val navigation: Observable<NavigationTarget>

    val uiEvents = DetailsUiEvents()

    init {
        val sources = DetailsSources(uiEvents, movieStorage)
        val sinks = main(sources, detailsArgs)

        state = LiveDataReactiveStreams.fromPublisher(sinks
                .ofType(DetailsSink.State::class.java)
                .map { it.state }
                .toFlowable(BackpressureStrategy.LATEST)
        )
        navigation = sinks
                .ofType(DetailsSink.Navigation::class.java)
                .map { it.target }
    }
}