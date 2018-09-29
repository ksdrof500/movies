package br.com.movies_tek.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import br.com.movies_tek.R
import br.com.movies_tek.data.MovieStorage
import br.com.movies_tek.data.SharedPrefs
import br.com.movies_tek.databinding.ActivityMainBinding
import br.com.movies_tek.di.ApplicationComponent
import br.com.movies_tek.ui.base.ActivityResult
import br.com.movies_tek.ui.base.BaseActivity
import br.com.movies_tek.ui.base.BaseFragment
import br.com.movies_tek.ui.main.view.MainFragment
import br.com.movies_tek.ui.main.viewmodel.MainViewModel
import br.com.movies_tek.ui.main.viewmodel.MainViewModelFactory
import br.com.movies_tek.MoviesApplication
import br.com.movies_tek.utils.bindTo
import br.com.movies_tek.utils.navigateTo
import javax.inject.Inject

class MainActivity : BaseActivity(), BaseFragment.ActivityListener {


    @Inject
    lateinit var sharedPrefs: SharedPrefs
    @Inject
    lateinit var movieStorage: MovieStorage
    private val component: ApplicationComponent by lazy { MoviesApplication.getAppComponent(this) }

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }
    private val sortOptions by lazy { makeSortOptions { getString(it) } }
    
    private val viewModel by lazy {
        val factory = MainViewModelFactory(sharedPrefs, movieStorage, sortOptions)
        ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportActionBar?.title = getString(R.string.title_popular)
                viewModel.uiEvents.sortSelections.accept(SortOption.SORT_POPULARITY.ordinal)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                supportActionBar?.title = getString(R.string.title_highest)
                viewModel.uiEvents.sortSelections.accept(SortOption.SORT_RATING.ordinal)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                supportActionBar?.title = getString(R.string.title_favorites)
                viewModel.uiEvents.sortSelections.accept(SortOption.SORT_FAVORITE.ordinal)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        component.inject(this)
        initViewModel()

        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            addFragment()
        }

        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.navigation.selectedItemId = R.id.navigation_home
    }

    private fun initViewModel() {
        viewModel.navigation
                .bindTo(lifecycle)
                .subscribe { navigateTo(this, it) }
    }

    private fun addFragment() {
        val fragment = MainFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.container_main, fragment, fragment.javaClass.canonicalName)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.uiEvents.activityResults.accept(ActivityResult(requestCode, resultCode, data))
    }
}
