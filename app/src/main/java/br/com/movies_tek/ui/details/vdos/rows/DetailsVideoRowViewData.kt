package br.com.movies_tek.ui.details.vdos.rows

import android.databinding.BaseObservable
import android.databinding.Bindable
import br.com.movies_tek.R

data class DetailsVideoRowViewData(
        @get:Bindable val key: String,
        @get:Bindable val name: String,
        @get:Bindable val site: String,
        @get:Bindable val size: Int,
        override val viewType: Int = R.layout.row_details_video
) : BaseObservable(), DetailsRowViewData