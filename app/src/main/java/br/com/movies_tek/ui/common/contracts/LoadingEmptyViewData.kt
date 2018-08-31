package br.com.movies_tek.ui.common.contracts

import android.databinding.Bindable
import android.databinding.Observable

interface LoadingEmptyViewData : Observable {
    @get:Bindable
    var loading: Boolean
    @get:Bindable
    var empty: Boolean
}