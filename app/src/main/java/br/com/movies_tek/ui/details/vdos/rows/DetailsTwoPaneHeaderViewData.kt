package br.com.movies_tek.ui.details.vdos.rows

import android.databinding.BaseObservable
import android.databinding.Bindable
import br.com.movies_tek.R

data class DetailsTwoPaneHeaderViewData(
        @get:Bindable val title: String,
        @get:Bindable val backdrop: String,
        override val viewType: Int = R.layout.row_details_two_pane_header
) : BaseObservable(), DetailsRowViewData