package br.com.movies_tek.ui.base

import android.R
import android.arch.lifecycle.LifecycleRegistry
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.movies_tek.MoviesApplication
import br.com.movies_tek.broadcast.ConnectivityReceiver
import br.com.movies_tek.utils.longSnackbarRed
import com.google.firebase.analytics.FirebaseAnalytics

data class ActivityResult(val requestCode: Int, val resultCode: Int, val data: Intent?)

abstract class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private val registry = LifecycleRegistry(this)

    private lateinit var connectivityReceiver: ConnectivityReceiver

    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun getLifecycle(): LifecycleRegistry = registry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

        connectivityReceiver = ConnectivityReceiver()
        registerReceiver(connectivityReceiver, intentFilter)

        MoviesApplication.instance.setConnectivityListener(this)
    }

    private fun showNoInternet() {
        longSnackbarRed(findViewById(R.id.content), getString(br.com.movies_tek.R.string.error_internet_connection))
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected)
            showNoInternet()
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(connectivityReceiver)
        } catch (ex: IllegalArgumentException) {
        }

    }


}