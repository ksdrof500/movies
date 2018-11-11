package br.com.movies_tek

import android.support.v7.app.AppCompatActivity
import android.app.Application
import br.com.movies_tek.broadcast.ConnectivityReceiver
import br.com.movies_tek.di.ApplicationComponent
import br.com.movies_tek.di.DaggerApplicationComponent
import br.com.movies_tek.di.modules.ApplicationModule
import br.com.movies_tek.di.modules.MovieServiceModule

class MoviesApplication : Application() {

    companion object {
        lateinit var instance: MoviesApplication

        fun getAppComponent(activity: AppCompatActivity): ApplicationComponent =
                (activity.application as MoviesApplication).appComponent
    }

    private val appComponent by lazy {
        DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .movieServiceModule(MovieServiceModule())
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

    }

    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

}
