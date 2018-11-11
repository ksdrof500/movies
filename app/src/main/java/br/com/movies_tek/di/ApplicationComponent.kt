package br.com.movies_tek.di

import android.app.Application
import android.content.SharedPreferences
import br.com.movies_tek.data.db.MovieDb
import br.com.movies_tek.data.service.TheMovieDbService
import br.com.movies_tek.di.modules.ApplicationModule
import br.com.movies_tek.di.modules.MovieServiceModule
import br.com.movies_tek.ui.details.view.DetailsActivity
import br.com.movies_tek.ui.main.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, MovieServiceModule::class])
interface ApplicationComponent {

    val application: Application

    val movieDb: MovieDb

    val sharedPreferences: SharedPreferences

    val theMovieDbService: TheMovieDbService

    fun inject(mainActivity: MainActivity)

    fun inject(detailsActivity: DetailsActivity)
}
