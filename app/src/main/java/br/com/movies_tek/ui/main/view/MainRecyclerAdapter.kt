package br.com.movies_tek.ui.main.view

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.movies_tek.R
import br.com.movies_tek.databinding.RowMovieBinding
import br.com.movies_tek.ui.base.BaseBindingViewHolder
import br.com.movies_tek.ui.common.viewholders.DefaultViewHolder
import br.com.movies_tek.ui.main.vdos.rows.MainRowMovieViewData
import br.com.movies_tek.ui.main.vdos.rows.MainRowViewData
import br.com.movies_tek.utils.setHeight
import com.jakewharton.rxrelay2.PublishRelay

class MovieViewHolder(binding: RowMovieBinding) : BaseBindingViewHolder<RowMovieBinding>(binding)

data class SelectedMovie(
        val id: Int,
        val title: String,
        val releaseDate: String,
        val overview: String,
        val voteAverage: Double,
        val poster: String?,
        val backdrop: String?,
        val posterView: View?
)

class GridRecyclerAdapter(
        private val posterHeight: Int,
        private val movieClicks: PublishRelay<SelectedMovie>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val movies = mutableListOf<MainRowViewData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.row_movie -> RowMovieBinding.inflate(inflater, parent, false).let { it ->
                MovieViewHolder(it).apply {
                    binding.root.setOnClickListener {
                        val rowData = movies[adapterPosition] as MainRowMovieViewData
                        val selectedMovie = SelectedMovie(
                                rowData.id,
                                rowData.title,
                                rowData.releaseDate,
                                rowData.overview,
                                rowData.voteAverage,
                                rowData.poster,
                                rowData.backdrop,
                                binding.ivPoster
                        )
                        movieClicks.accept(selectedMovie)
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
        if (viewType == R.layout.row_movie) {
            val movieRow = holder as MovieViewHolder
            movieRow.binding.flContainer.setHeight(posterHeight)
            movieRow.binding.viewData = movies[position] as MainRowMovieViewData
            movieRow.binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int = movies[position].viewType

    override fun getItemCount(): Int = movies.size

    fun swapData(newMovies: List<MainRowViewData>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = movies[oldItemPosition]
                val newItem = newMovies[newItemPosition]

                return when {
                    oldItem.viewType != newItem.viewType -> false
                    oldItem.viewType == R.layout.row_progress -> true
                    else -> (oldItem as MainRowMovieViewData).id == (newItem as MainRowMovieViewData).id
                }
            }

            override fun getOldListSize(): Int = movies.size

            override fun getNewListSize(): Int = newMovies.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    movies[oldItemPosition] == newMovies[newItemPosition]
        })

        movies.clear()
        movies.addAll(newMovies)
        diffResult.dispatchUpdatesTo(this)
    }
}
