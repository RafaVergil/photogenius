package adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.widget.Toast
import fragments.MediaFragment
import models.MediaModel
import utils.UTILS
import java.util.*

class MediaPagerAdapter(fm: FragmentManager?,
                        val data: List<MediaModel>,
                        private val callback: IMediaCallback) :
        FragmentStatePagerAdapter(fm) {

    interface IMediaCallback {
        fun onMediaClick()
        fun onMediaShow(mediaModel: MediaModel)
    }

    override fun getItem(position: Int): Fragment {
        val mediaFrag = MediaFragment.init(data[position], callback)
        callback.onMediaShow(data[position])
        return mediaFrag
    }

    override fun getCount(): Int {
        return data.size
    }

}