package br.com.rafaelverginelli.photogenius

import abstractions.CustomAppCompatActivity
import adapters.MediaPagerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import com.otaliastudios.zoom.ZoomLayout
import fragments.MediaFragment
import kotlinx.android.synthetic.main.activity_media.*
import models.MediaModel
import transformers.DepthPageTransformer
import utils.UTILS


class MediaActivity : CustomAppCompatActivity() {

    private var adapter: MediaPagerAdapter? = null
    private var showInfo = false

    companion object {

        private var data: List<MediaModel> = ArrayList()

        //todo change later for bundle parameter passing
        fun setMediaData(data: List<MediaModel>) {
            this.data = data
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        adapter = MediaPagerAdapter(supportFragmentManager, data,
                object: MediaPagerAdapter.IMediaCallback{

                    override fun onMediaClick() {
                        showInfo = !showInfo
                        animateInfoAlpha()
                    }

                    override fun onMediaShow(mediaModel: MediaModel) {
                        txtLikeCount.text = mediaModel.likes.count.toString()
                        txtCommentCount.text = mediaModel.comments.count.toString()
                    }
                })
        viewPager.adapter = adapter
        viewPager.setPageTransformer(true, DepthPageTransformer())
    }

    //todo not working, check later
    private fun animateInfoAlpha(){
        val to: Float = if(showInfo) 1f else 0f
        val alpha = ObjectAnimator
                .ofFloat(rlInfo, View.ALPHA, rlInfo.alpha, to)
        alpha.duration = 250
        alpha.start()
    }


}
