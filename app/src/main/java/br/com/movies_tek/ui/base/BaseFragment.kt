package br.com.movies_tek.ui.base

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v4.app.Fragment

abstract class BaseFragment<T : BaseFragment.ActivityListener> : Fragment(), LifecycleOwner {

    private lateinit var activityListener: T

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        @Suppress("UNCHECKED_CAST")
        activityListener = context as T
    }

    interface ActivityListener
}