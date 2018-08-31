package br.com.movies_tek.ui.details.vdos.rows

import android.databinding.BaseObservable
import android.databinding.Bindable
import br.com.movies_tek.R
import br.com.movies_tek.ui.common.contracts.HeaderRowViewData

data class DetailsHeaderRowViewData(
        @get:Bindable override val header: Int,
        override val viewType: Int = R.layout.row_header
) : BaseObservable(), DetailsRowViewData, HeaderRowViewData