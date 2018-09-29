package br.com.movies_tek.ui.login.component

import android.app.Activity

interface LoginDelegate {
    fun login()
    fun moveForward(activity: Activity)

}