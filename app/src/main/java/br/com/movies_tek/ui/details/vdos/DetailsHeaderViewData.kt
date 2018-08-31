package br.com.movies_tek.ui.details.vdos

import android.databinding.BaseObservable
import android.databinding.Bindable
import br.com.movies_tek.BR

class DetailsHeaderViewData : BaseObservable() {
    @get:Bindable
    var title: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }

    @get:Bindable
    var backdrop: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.backdrop)
        }

    @get:Bindable
    var favoured: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.favoured)
        }
}