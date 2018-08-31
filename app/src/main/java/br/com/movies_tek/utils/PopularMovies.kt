package br.com.movies_tek.utils

import android.app.Activity
import android.app.Application
import br.com.movies_tek.di.ApplicationComponent
import br.com.movies_tek.di.DaggerApplicationComponent
import br.com.movies_tek.di.modules.ApplicationModule
import br.com.movies_tek.di.modules.MovieServiceModule

class PopularMovies : Application() {

    companion object {
        fun getAppComponent(activity: Activity): ApplicationComponent =
                (activity.application as PopularMovies).appComponent
    }

    private val appComponent by lazy {
        DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .movieServiceModule(MovieServiceModule())
                .build()
    }

}
