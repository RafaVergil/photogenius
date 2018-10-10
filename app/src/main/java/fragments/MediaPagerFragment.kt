package fragments

import adapters.MediaPagerAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import br.com.rafaelverginelli.photogenius.R
import kotlinx.android.synthetic.main.fragment_media_pager.*
import models.MediaModel
import utils.CONSTANTS
import utils.UTILS

class MediaPagerFragment : Fragment(){

    private var adapter: MediaPagerAdapter? = null

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_media_pager, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = MediaPagerAdapter(activity!!.supportFragmentManager, data,
                object: MediaPagerAdapter.IMediaCallback{
                    override fun onMediaShow(mediaModel: MediaModel, position: Int) {
                        if(!UTILS.checkConnectivity(activity!!) &&
                                mediaModel.type.toLowerCase() ==
                                CONSTANTS.KEY_INSTAGRAM_MEDIA_TOKEN_TYPE_VIDEO){
                            Toast.makeText(activity!!, R.string.no_connection,
                                    Toast.LENGTH_LONG).show()

                        }
                        txtLikeCount.text = mediaModel.likes.count.toString()
                        txtCommentCount.text = mediaModel.comments.count.toString() }})
        viewPager.adapter = adapter
        viewPager.currentItem = index
    }



}