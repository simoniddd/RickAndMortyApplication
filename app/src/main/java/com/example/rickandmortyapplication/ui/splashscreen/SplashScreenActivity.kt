package com.example.rickandmortyapplication.ui.splashscreen

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.rickandmortyapplication.MainActivity
import com.example.rickandmortyapplication.R

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContentView(R.layout.activity_splash_screen)

        val lottieAnimationView: LottieAnimationView = findViewById(R.id.lottieAnimationView)

        lottieAnimationView.playAnimation()

        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(p0: Animator) {
                // do nothing
            }

            override fun onAnimationEnd(p0: Animator) {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            }

            override fun onAnimationCancel(p0: Animator) {
                // do nothing
            }

            override fun onAnimationRepeat(p0: Animator) {
                // do nothing
            }
        })
    }
}