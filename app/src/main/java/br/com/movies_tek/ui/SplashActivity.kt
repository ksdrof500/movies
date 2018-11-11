package br.com.movies_tek.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.AnimationUtils
import br.com.movies_tek.R
import br.com.movies_tek.databinding.ActivitySplashBinding
import br.com.movies_tek.ui.main.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor


class SplashActivity : AppCompatActivity() {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivitySplashBinding>(this, R.layout.activity_splash)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        animationDown()

        GlobalScope.launch {
            delay(resources.getInteger(R.integer.delay_splash).toLong())
            animationUp()
            delay(resources.getInteger(R.integer.delay_splash).toLong())
            startActivity(intentFor<MainActivity>().clearTop().clearTask())
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
