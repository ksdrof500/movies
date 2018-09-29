package br.com.movies_tek.ui.login.view

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import br.com.movies_tek.R
import br.com.movies_tek.databinding.ActivityLoginBinding
import br.com.movies_tek.databinding.ActivityMainBinding
import br.com.movies_tek.databinding.ActivitySplashBinding
import br.com.movies_tek.ui.base.ActivityResult
import br.com.movies_tek.ui.base.BaseActivity
import br.com.movies_tek.ui.login.component.LoginDelegate
import br.com.movies_tek.ui.login.component.RC_SIGN_IN
import br.com.movies_tek.ui.login.viewmodel.LoginViewModel
import br.com.movies_tek.ui.login.viewmodel.LoginViewModelFactory
import br.com.movies_tek.ui.main.viewmodel.MainViewModel
import br.com.movies_tek.ui.main.viewmodel.MainViewModelFactory
import br.com.movies_tek.utils.bindTo
import br.com.movies_tek.utils.longSnackbarRed
import br.com.movies_tek.utils.navigateTo
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener, LoginDelegate {

    private var mGoogleApiClient: GoogleApiClient? = null

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
    }

    private val viewModel by lazy {
        val factory = LoginViewModelFactory(FirebaseAuth.getInstance(), this)
        ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.loginVM = viewModel
        configureAuth()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.navigation
                .bindTo(lifecycle)
                .subscribe { navigateTo(this, it) }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        longSnackbarRed(binding.root, getString(R.string.error_internet_connection))
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                if (account != null) {
                    viewModel.firebaseAuthWithGoogle(account, this)
                }
            }
        }
    }

    override fun login() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun moveForward(activity: Activity) {
        startActivity(Intent(this, activity.javaClass))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun configureAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }
}
