package br.com.movies_tek.ui.details.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import br.com.movies_tek.R
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.databinding.ActivityMovieDetailsBinding
import br.com.movies_tek.di.ApplicationComponent
import br.com.movies_tek.ui.base.BaseActivity
import br.com.movies_tek.ui.base.BaseFragment
import br.com.movies_tek.ui.details.component.DetailsState
import br.com.movies_tek.ui.details.vdos.DetailsHeaderViewData
import br.com.movies_tek.ui.details.viewmodel.DetailsViewModel
import br.com.movies_tek.ui.details.viewmodel.DetailsViewModelFactory
import br.com.movies_tek.utils.KEY_ACTIVITY_ARGS
import br.com.movies_tek.MoviesApplication
import br.com.movies_tek.utils.FirebaseEvents
import br.com.movies_tek.utils.bindTo
import br.com.movies_tek.utils.navigateTo
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import javax.inject.Inject

@PaperParcel
data class DetailsArgs(
        val id: Int,
        val title: String,
        val releaseDate: String,
        val overview: String,
        val voteAverage: Double,
        val poster: String?,
        val backdrop: String?,
        val fromFavList: Boolean
) : PaperParcelable {
    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR = PaperParcelDetailsArgs.CREATOR
    }
}

class DetailsActivity : BaseActivity(), BaseFragment.ActivityListener {

    @Inject
    lateinit var movieStorage: MovieStorage

    private val viewModel by lazy {
        val args = intent.getParcelableExtra<DetailsArgs>(KEY_ACTIVITY_ARGS)
        val factory = DetailsViewModelFactory(movieStorage, args)
        ViewModelProviders.of(this, factory).get(DetailsViewModel::class.java)
    }
    private val component: ApplicationComponent by lazy { MoviesApplication.getAppComponent(this) }

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMovieDetailsBinding>(this, R.layout.activity_movie_details)
    }

    private val viewData = DetailsHeaderViewData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportPostponeEnterTransition()

        component.inject(this)
        binding.viewData = viewData

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        initViewModel()
        binding.fabDetailsFavorite.setOnClickListener {
            firebaseAnalytics.logEvent(FirebaseEvents.BUTTON_FAVORITES, Bundle())
            viewModel.uiEvents.favClicks.accept(Unit) }

        if (savedInstanceState == null) {
            addFragment()
        }
    }

    private fun initViewModel() {
        viewModel.state.observe(this, Observer<DetailsState> { it ->
            it?.let { render(it) }
        })
        viewModel.navigation
                .bindTo(lifecycle)
                .subscribe { navigateTo(this, it) }
    }

    private fun render(state: DetailsState) {
        viewData.title = state.title
        viewData.backdrop = state.backdrop
        viewData.favoured = state.favoured
        if (state.snackbar.show) {
            Snackbar.make(binding.container, state.snackbar.message, Snackbar.LENGTH_LONG).show()
            viewModel.uiEvents.snackbarShown.accept(Unit)
        }
    }

    private fun addFragment() {
        val args = intent.getParcelableExtra<DetailsArgs>(KEY_ACTIVITY_ARGS)
        val fragment = DetailsFragment.newInstance(args)
        supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment, fragment.javaClass.canonicalName)
                .commit()
    }
}
