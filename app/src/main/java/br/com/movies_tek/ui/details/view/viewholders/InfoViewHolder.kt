package br.com.movies_tek.ui.details.view.viewholders

import android.support.annotation.IntegerRes
import android.widget.TextView
import br.com.movies_tek.databinding.RowDetailsInfoBinding
import br.com.movies_tek.utils.expandOrCollapse
import br.com.movies_tek.ui.base.BaseBindingViewHolder

class InfoViewHolder(
        binding: RowDetailsInfoBinding,
        @IntegerRes maxLinesCollapsed: Int
) : BaseBindingViewHolder<RowDetailsInfoBinding>(binding) {

    init {
        binding.tvDetailsPlot.setOnClickListener { (it as TextView).expandOrCollapse(maxLinesCollapsed) }
    }
}