package br.com.movies_tek.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.view.animation.AnimationUtils
import br.com.movies_tek.R
import br.com.movies_tek.databinding.ActivitySplashBinding
import br.com.movies_tek.ui.login.view.LoginActivity
import br.com.movies_tek.ui.main.MainActivity
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch


class SplashActivity : AppCompatActivity() {

    internal val intent by lazy { Intent(this, LoginActivity::class.java) }
    private val bundle by lazy {
        ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
    }

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivitySplashBinding>(this, R.layout.activity_splash)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        animationDown()

        launch {
            delay(resources.getInteger(R.integer.delay_splash))
            animationUp()
            delay(resources.getInteger(R.integer.delay_splash))
            startActivity(intent, bundle)
            finish()
        }
    }

    private fun animationUp() {
        val a = AnimationUtils.loadAnimation(this, R.anim.anim_text_spash_up)
        a.reset()
        binding.textSplash.startAnimation(a)
    }

    private fun animationDown() {
        val a = AnimationUtils.loadAnimation(this, R.anim.anim_text_spash_down)
        binding.textSplash.startAnimation(a)
    }
}
