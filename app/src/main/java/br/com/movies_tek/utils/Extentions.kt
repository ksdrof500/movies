package br.com.movies_tek.utils

import android.animation.ObjectAnimator
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
import android.view.View
import android.widget.TextView
import io.reactivex.Observable
import java.text.DateFormat
import java.util.*

fun <T> Observable<T>.log(tag: String): Observable<T> = doOnNext { timber.log.Timber.d("$tag: $it") }

fun <T> Observable<T>.bindTo(lifecycleRegistry: LifecycleRegistry): Observable<T> = compose { it ->
    it
            .takeWhile { lifecycleRegistry.currentState != Lifecycle.State.DESTROYED }
            .filter { lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED) }
}

private const val COLLAPSE_EXPAND_ANIM_TIME: Long = 300
private const val MAX_LINES_EXPANDED = 500

fun TextView.expandOrCollapse(maxLinesCollapsed: Int) {
    val value = if (maxLines == maxLinesCollapsed) MAX_LINES_EXPANDED else maxLinesCollapsed
    val anim = ObjectAnimator.ofInt(this, "maxLines", value)
    anim.duration = COLLAPSE_EXPAND_ANIM_TIME
    anim.start()
}

fun View.setHeight(height: Int) {
    val params = layoutParams
    params.height = height
    layoutParams = params
}

fun Date.formatLong(): String = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault()).format(this)