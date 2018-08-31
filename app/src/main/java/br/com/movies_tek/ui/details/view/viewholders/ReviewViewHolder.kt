package br.com.movies_tek.ui.details.view.viewholders

import android.support.annotation.IntegerRes
import android.widget.TextView
import br.com.movies_tek.databinding.RowDetailsReviewBinding
import br.com.movies_tek.ui.base.BaseBindingViewHolder
import br.com.movies_tek.utils.expandOrCollapse

class ReviewViewHolder(
        binding: RowDetailsReviewBinding,
        @IntegerRes maxLinesCollapsed: Int
) : BaseBindingViewHolder<RowDetailsReviewBinding>(binding) {

    init {
        binding.tvDetailsReviewContent.setOnClickListener { (it as TextView).expandOrCollapse(maxLinesCollapsed) }
    }
}