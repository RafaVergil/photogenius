package br.com.rafaelverginelli.photogenius

import abstractions.CustomAppCompatActivity
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : CustomAppCompatActivity() {

    val animDuration: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        StartAnimation()
    }

    fun StartAnimation() {
        imgLogo.scaleX = 10f
        imgLogo.scaleY = 10f
        imgLogo.alpha = 0f

        val animScaleX: ObjectAnimator = ObjectAnimator.
                ofFloat(imgLogo, "scaleX", imgLogo.scaleX, 1f)
        animScaleX.duration = animDuration
        animScaleX.start()

        val animScaleY: ObjectAnimator = ObjectAnimator.
                ofFloat(imgLogo, "scaleY", imgLogo.scaleX, 1f)
        animScaleY.duration = animDuration
        animScaleY.start()

        val animAlpha: ObjectAnimator = ObjectAnimator.
                ofFloat(imgLogo, "alpha", imgLogo.alpha, 1f)
        animAlpha.duration = animDuration
        animAlpha.start()

        val splashDuration = Handler()
        splashDuration.postDelayed(object: Runnable{
            override fun run() {

                val transitionName = getString(R.string.transition_logo)
                val intent = Intent(this@SplashActivity, SignInActivity::class.java)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@SplashActivity,
                        imgLogo,
                        transitionName)

                ActivityCompat.startActivity(
                        this@SplashActivity, intent, options.toBundle())

            }
        }, animDuration * 2) //The total duration time of the Splash is 2 seconds.
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}
