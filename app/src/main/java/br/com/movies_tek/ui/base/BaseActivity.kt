package br.com.movies_tek.ui.base

import android.arch.lifecycle.LifecycleRegistry
import android.content.Intent
import android.support.v7.app.AppCompatActivity

data class ActivityResult(val requestCode: Int, val resultCode: Int, val data: Intent?)

abstract class BaseActivity : AppCompatActivity() {

    private val registry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry = registry
}