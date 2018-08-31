package br.com.movies_tek.utils

import android.content.res.Resources
import br.com.movies_tek.R

private const val POSTER_ASPECT_RATIO = 0.675

/**
 * Returns the correct poster height for a given width and number of columns, respecting the
 * default aspect ratio.

 * @param res resources
 * @param useTwoPane phone or table mode
 * @return the correct poster height in pixels
 */
fun calcPosterHeight(
        res: Resources,
        useTwoPane: Boolean = res.getBoolean(R.bool.use_two_pane_layout),
        spanCount: Int = res.getInteger(R.integer.span_count)
): Int {
    val itemWidth = getLayoutWidth(res, useTwoPane) / spanCount
    return (itemWidth / POSTER_ASPECT_RATIO).toInt()
}

private fun getLayoutWidth(res: Resources, useTwoPane: Boolean): Int = getScreenWidth(res).let {
    if (useTwoPane) it / 100 * res.getInteger(R.integer.two_pane_list_width_percentage)
    else it
}

private fun getScreenWidth(res: Resources): Int = res.displayMetrics.widthPixels
