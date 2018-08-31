package br.com.movies_tek.ui.main.vdos

import android.databinding.BaseObservable
import android.databinding.Bindable
import br.com.movies_tek.BR
import br.com.movies_tek.ui.common.contracts.LoadingEmptyViewData

class MainViewData : BaseObservable(), LoadingEmptyViewData {
    @get:Bindable
    override var loading: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.loading)
        }

    @get:Bindable
    override var empty: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.empty)
        }

    @get:Bindable
    var refreshEnabled: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.refreshEnabled)
        }

    @get:Bindable
    var refreshing: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.refreshing)
        }

    @get:Bindable
    var loadingMore: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.loadingMore)
        }
}