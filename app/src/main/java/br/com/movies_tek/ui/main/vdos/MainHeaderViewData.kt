package br.com.movies_tek.ui.main.vdos

import android.databinding.BaseObservable
import android.databinding.Bindable
import br.com.movies_tek.BR

class MainHeaderViewData : BaseObservable() {
    @get:Bindable
    var movieSelected: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.movieSelected)
        }

    @get:Bindable
    var favoured: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.favoured)
        }
}