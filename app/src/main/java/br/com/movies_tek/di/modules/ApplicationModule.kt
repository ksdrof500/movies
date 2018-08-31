package br.com.movies_tek.di.modules

import android.app.Application
import android.arch.persistence.room.Room
import android.content.SharedPreferences
import android.preference.PreferenceManager
import br.com.movies_tek.data.db.MovieDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    internal fun providesApplication(): Application = application

    @Provides
    @Singleton
    internal fun providesMovieDb(application: Application): MovieDb =
            Room.databaseBuilder(application, MovieDb::class.java, "movies.db").build()

    @Provides
    @Singleton
    internal fun providesSharedPreferences(application: Application): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(application)
}
