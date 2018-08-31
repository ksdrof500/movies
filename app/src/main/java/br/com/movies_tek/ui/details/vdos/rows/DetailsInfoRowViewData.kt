package br.com.movies_tek.ui.details.vdos.rows

import android.databinding.BaseObservable
import android.databinding.Bindable
import br.com.movies_tek.R

data class DetailsInfoRowViewData(
        @get:Bindable val poster: String?,
        @get:Bindable val date: String?,
        @get:Bindable val rating: Double?,
        @get:Bindable val plot: String?,
        @get:Bindable val transitionEnabled: Boolean = true,
        override val viewType: Int = R.layout.row_details_info
) : BaseObservable(), DetailsRowViewData