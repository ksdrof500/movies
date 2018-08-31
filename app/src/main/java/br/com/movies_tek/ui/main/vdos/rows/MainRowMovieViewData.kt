package br.com.movies_tek.ui.main.vdos.rows

import android.databinding.BaseObservable
import android.databinding.Bindable
import br.com.movies_tek.R

data class MainRowMovieViewData(
        val id: Int,
        @get:Bindable val title: String,
        val overview: String,
        @get:Bindable val releaseDate: String,
        val voteAverage: Double,
        @get:Bindable val poster: String?,
        val backdrop: String?,
        @get:Bindable val rating: Double?,
        override val viewType: Int = R.layout.row_movie
) : BaseObservable(), MainRowViewData
