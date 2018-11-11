package br.com.movies_tek.ui.main.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.movies_tek.R
import br.com.movies_tek.databinding.FragmentMovieMainBinding
import br.com.movies_tek.ui.base.BaseFragment
import br.com.movies_tek.ui.main.SortOption
import br.com.movies_tek.ui.main.component.MainState
import br.com.movies_tek.ui.main.vdos.MainViewData
import br.com.movies_tek.ui.main.viewmodel.MainViewModel
import br.com.movies_tek.utils.calcPosterHeight
import com.mugen.Mugen
import com.mugen.MugenCallbacks

class MainFragment : BaseFragment<BaseFragment.ActivityListener>() {

    private val viewModel: MainViewModel by lazy { ViewModelProviders.of(activity!!).get(MainViewModel::class.java) }
    private val viewData = MainViewData()
    private val recyclerAdapter: GridRecyclerAdapter by lazy {
        GridRecyclerAdapter(calcPosterHeight(resources), viewModel.uiEvents.movieClicks)
    }
    private lateinit var binding: FragmentMovieMainBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieMainBinding.inflate(inflater, container, false)
        binding.viewData = viewData
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        binding.srlGrid.setOnRefreshListener { viewModel.uiEvents.refreshSwipes.accept(Unit) }
    }

    private fun setupRecyclerView() {
        val spanCount = resources.getInteger(br.com.movies_tek.R.integer.span_count)
        val layoutManager = GridLayoutManager(activity, spanCount)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                    if (recyclerAdapter.getItemViewType(position) == R.layout.row_progress) spanCount
                    else 1
        }
        binding.rvGrid.layoutManager = layoutManager
        binding.rvGrid.setHasFixedSize(true)
        val itemPadding = resources.getDimensionPixelSize(R.dimen.grid_padding)
        binding.rvGrid.addItemDecoration(MainItemPadding(itemPadding))
        binding.rvGrid.adapter = recyclerAdapter
        Mugen.with(binding.rvGrid, object : MugenCallbacks {
            override fun onLoadMore() {
                if (viewData.refreshEnabled) viewModel.uiEvents.loadMore.accept(Unit)
            }

            override fun isLoading(): Boolean = viewData.loading || viewData.refreshing || viewData.loadingMore

            override fun hasLoadedAllItems(): Boolean = false
        }).start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.observe(this, Observer<MainState> { it ->
            it?.let { render(it) }
        })
    }

    private fun render(state: MainState) {
        viewData.refreshEnabled = state.sort.option != SortOption.SORT_FAVORITE
        viewData.empty = state.empty
        viewData.loading = state.loading
        viewData.refreshing = state.refreshing
        viewData.loadingMore = state.loadingMore
        recyclerAdapter.swapData(state.movies)

        if (state.snackbar.show) {
            Snackbar.make(binding.rvGrid, state.snackbar.message, Snackbar.LENGTH_LONG).show()
            viewModel.uiEvents.snackbarShown.accept(Unit)
        }
    }
}
