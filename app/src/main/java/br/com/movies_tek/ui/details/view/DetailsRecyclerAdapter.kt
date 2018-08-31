package br.com.movies_tek.ui.details.view

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.movies_tek.R
import br.com.movies_tek.databinding.*
import br.com.movies_tek.ui.common.contracts.HeaderRowViewData
import br.com.movies_tek.ui.common.viewholders.DefaultViewHolder
import br.com.movies_tek.ui.common.viewholders.HeaderViewHolder
import br.com.movies_tek.ui.details.vdos.rows.*
import br.com.movies_tek.ui.details.view.viewholders.InfoViewHolder
import br.com.movies_tek.ui.details.view.viewholders.ReviewViewHolder
import br.com.movies_tek.ui.details.view.viewholders.TwoPaneHeaderViewHolder
import br.com.movies_tek.ui.details.view.viewholders.VideoViewHolder
import com.jakewharton.rxrelay2.PublishRelay

class DetailsRecyclerAdapter(
        val videoClicks: PublishRelay<DetailsVideoRowViewData>,
        val posterListener: PosterLoadListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val movieDetails = mutableListOf<DetailsRowViewData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.row_details_two_pane_header ->
                TwoPaneHeaderViewHolder(RowDetailsTwoPaneHeaderBinding.inflate(inflater, parent, false))

            R.layout.row_details_info ->
                InfoViewHolder(RowDetailsInfoBinding.inflate(inflater, parent, false), parent.resources.getInteger(R.integer.plot_max_lines_collapsed))

            R.layout.row_header -> HeaderViewHolder(RowHeaderBinding.inflate(inflater, parent, false))

            R.layout.row_details_review ->
                ReviewViewHolder(RowDetailsReviewBinding.inflate(inflater, parent, false), parent.resources.getInteger(R.integer.review_content_max_lines_collapsed))

            R.layout.row_details_video ->
                RowDetailsVideoBinding.inflate(inflater, parent, false).let { it ->
                    VideoViewHolder(it).apply {
                        binding.root.setOnClickListener {
                            val viewData = movieDetails[adapterPosition] as DetailsVideoRowViewData
                            videoClicks.accept(viewData)
                        }
                    }
                }

            R.layout.row_progress -> DefaultViewHolder(inflater.inflate(R.layout.row_progress, parent, false))
            else -> throw RuntimeException("there is no type that matches the type $viewType, " +
                    "make sure you are using types correctly")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            R.layout.row_details_two_pane_header -> {
                val twoPaneHeaderHolder = holder as TwoPaneHeaderViewHolder
                twoPaneHeaderHolder.binding.viewData = movieDetails[position] as DetailsTwoPaneHeaderViewData
                twoPaneHeaderHolder.binding.executePendingBindings()
            }

            R.layout.row_details_info -> {
                val infoHolder = holder as InfoViewHolder
                infoHolder.binding.viewData = movieDetails[position] as DetailsInfoRowViewData
                infoHolder.binding.posterListener = posterListener
                infoHolder.binding.executePendingBindings()
            }

            R.layout.row_header -> {
                val headerHolder = holder as HeaderViewHolder
                headerHolder.binding.viewData = movieDetails[position] as HeaderRowViewData
                headerHolder.binding.executePendingBindings()
            }

            R.layout.row_details_review -> {
                val reviewHolder = holder as ReviewViewHolder
                reviewHolder.binding.viewData = movieDetails[position] as DetailsReviewRowViewData
                reviewHolder.binding.executePendingBindings()
            }

            R.layout.row_details_video -> {
                val videoHolder = holder as VideoViewHolder
                videoHolder.binding.viewData = movieDetails[position] as DetailsVideoRowViewData
                videoHolder.binding.executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int = movieDetails[position].viewType

    override fun getItemCount(): Int = movieDetails.size

    fun swapData(newDetails: List<DetailsRowViewData>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = movieDetails[oldItemPosition]
                val newItem = newDetails[newItemPosition]

                if (oldItem.viewType != newItem.viewType) {
                    return false
                }

                return true
            }

            override fun getOldListSize(): Int = movieDetails.size

            override fun getNewListSize(): Int = newDetails.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    movieDetails[oldItemPosition] == newDetails[newItemPosition]
        })

        movieDetails.clear()
        movieDetails.addAll(newDetails)
        diffResult.dispatchUpdatesTo(this)
    }
}