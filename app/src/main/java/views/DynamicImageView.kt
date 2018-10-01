package views

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.ImageView
import br.com.rafaelverginelli.photogenius.R

// This view is an ImageView that makes transitions between a predefined set of images.

class DynamicImageView : ImageView {

    private val timeToFade: Long = 5000
    private val transitionTime: Long = 2000
    val imageSetIds: IntArray = intArrayOf(
        R.drawable.bg_pic_01,
        R.drawable.bg_pic_02,
        R.drawable.bg_pic_03,
        R.drawable.bg_pic_04,
        R.drawable.bg_pic_05,
        R.drawable.bg_pic_06,
        R.drawable.bg_pic_07
    )
    var currentImageId: Int = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        setImageDrawable( ContextCompat.getDrawable(context, imageSetIds[currentImageId]) )
        fadeOut()
    }

    fun fadeOut(){
        val nextImage: ObjectAnimator = ObjectAnimator.ofFloat(this@DynamicImageView,
                "alpha", 1f, 0f)
        nextImage.startDelay = timeToFade
        nextImage.duration = transitionTime
        nextImage.addListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                currentImageId =
                        if(currentImageId >= (imageSetIds.size -1)) 0 else (currentImageId + 1)

                setImageDrawable( ContextCompat.getDrawable(context, imageSetIds[currentImageId]) )
                fadeIn()
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {

            }

        })
        nextImage.start()
    }

    fun fadeIn(){
        val nextImage: ObjectAnimator = ObjectAnimator.ofFloat(this@DynamicImageView,
                "alpha", 0f, 1f)
        nextImage.duration = transitionTime
        nextImage.addListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                fadeOut()
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {

            }

        })
        nextImage.start()
    }

}

