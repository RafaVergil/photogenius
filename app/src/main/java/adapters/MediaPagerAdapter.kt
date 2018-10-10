package adapters

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import fragments.MediaFragment
import models.MediaModel

class MediaPagerAdapter(fm: FragmentManager?,
                        val data: List<MediaModel>,
                        private val callback: IMediaCallback) :
        FragmentStatePagerAdapter(fm) {

    interface IMediaCallback {
        fun onMediaShow(mediaModel: MediaModel, position: Int)
    }

    override fun getItem(position: Int): MediaFragment? {
        val mediaFrag = MediaFragment.init(data[position], callback)
        callback.onMediaShow(data[position], position)
        return mediaFrag
    }

    override fun getCount(): Int {
        return data.size
    }

}