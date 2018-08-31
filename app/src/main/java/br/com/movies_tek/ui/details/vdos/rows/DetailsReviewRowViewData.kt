package br.com.movies_tek.ui.details.vdos.rows

import android.databinding.BaseObservable
import android.databinding.Bindable
import br.com.movies_tek.R

data class DetailsReviewRowViewData(
        @get:Bindable val author: String,
        @get:Bindable val content: String,
        override val viewType: Int = R.layout.row_details_review
) : BaseObservable(), DetailsRowViewData