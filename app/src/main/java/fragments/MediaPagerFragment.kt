package fragments

import adapters.MediaPagerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.rafaelverginelli.photogenius.R
import kotlinx.android.synthetic.main.fragment_media_pager.*
import models.MediaModel

class MediaPagerFragment : Fragment(){

    private var adapter: MediaPagerAdapter? = null
    private var showInfo = false

    private var index : kotlin.Int = 0
    private var data: List<MediaModel> = ArrayList()

    companion object {

        fun newInstance(index: kotlin.Int, data: List<MediaModel>) : MediaPagerFragment {
            val frag = MediaPagerFragment()
            frag.index = index
            frag.data = data
            return frag
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_media_pager, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = MediaPagerAdapter(activity!!.supportFragmentManager, data,
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
        viewPager.currentItem = index
//        viewPager.setPageTransformer(true, DepthPageTransformer())
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