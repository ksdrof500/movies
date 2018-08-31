package br.com.movies_tek.ui.details.vdos

import android.databinding.BaseObservable
import android.databinding.Bindable
import br.com.movies_tek.BR

class DetailsViewData : BaseObservable() {
    @get:Bindable
    var refreshing: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.refreshing)
        }

    @get:Bindable
    var refreshEnabled: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.refreshEnabled)
        }
}