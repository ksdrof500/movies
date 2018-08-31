package br.com.movies_tek.ui.details.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.movies_tek.R
import br.com.movies_tek.databinding.FragmentMovieDetailsBinding
import br.com.movies_tek.ui.base.BaseFragment
import br.com.movies_tek.ui.details.component.DetailsState
import br.com.movies_tek.ui.details.vdos.DetailsViewData
import br.com.movies_tek.ui.details.viewmodel.DetailsViewModel


const val KEY_ARGS = "KEY_ARGS"

class DetailsFragment : BaseFragment<BaseFragment.ActivityListener>(),
        PosterLoadListener {

    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(DetailsViewModel::class.java) }

    private val viewData = DetailsViewData()

    private val recyclerAdapter by lazy { DetailsRecyclerAdapter(viewModel.uiEvents.videoClicks, this) }

    private lateinit var binding: FragmentMovieDetailsBinding

    companion object {
        fun newInstance(detailsArgs: DetailsArgs): DetailsFragment {
            val args = Bundle().apply { putParcelable(KEY_ARGS, detailsArgs) }
            return DetailsFragment().apply { arguments = args }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        binding.viewData = viewData
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvDetails.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        binding.rvDetails.layoutManager = layoutManager
        val itemDecoration = ReviewsItemDecoration(context!!, R.layout.row_details_review)
        binding.rvDetails.addItemDecoration(itemDecoration)
        binding.rvDetails.adapter = recyclerAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.srlDetailsFav.setOnRefreshListener { viewModel.uiEvents.updateSwipes.accept(Unit) }
        viewData.refreshEnabled = arguments!!.getParcelable<DetailsArgs>(KEY_ARGS).fromFavList
        viewModel.state.observe(this, Observer<DetailsState> { it ->
            it?.let { render(it) }
        })
    }

    private fun render(state: DetailsState) {
        recyclerAdapter.swapData(state.details)
        viewData.refreshing = state.updating
    }

    override fun onPosterLoaded() {
        ActivityCompat.startPostponedEnterTransition(activity!!)
    }
}