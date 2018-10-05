package fragments

import adapters.MediaPagerAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import br.com.rafaelverginelli.photogenius.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.otaliastudios.zoom.ZoomLayout
import models.MediaModel
import utils.CONSTANTS


class MediaFragment : Fragment(){

    private var media: MediaModel? = null
    private var callback: MediaPagerAdapter.IMediaCallback? = null

    companion object {
        fun init(mediaModel: MediaModel, callback: MediaPagerAdapter.IMediaCallback): MediaFragment {

            val mediaFrag = MediaFragment()
            mediaFrag.callback = callback
            val args = Bundle()
            args.putSerializable(CONSTANTS.KEY_MEDIA_BUNDLE, mediaModel)
            mediaFrag.arguments = args
            return mediaFrag

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        media = if(arguments != null) {
            arguments!!.getSerializable(CONSTANTS.KEY_MEDIA_BUNDLE) as MediaModel
        } else { null }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layoutView = inflater.inflate(R.layout.fragment_fs_media, container,
                false)

        if(context != null && media != null) {
            val zoomLayout = layoutView.findViewById(R.id.zoomLayout) as ZoomLayout
            zoomLayout.zoomTo(1f, false)
            val imgCard = layoutView.findViewById(R.id.imgCard) as ImageView

            Glide.with(context!!)
                    .load(media!!.images.standard_resolution.url)
                    .apply(RequestOptions()
                            .placeholder(R.mipmap.ic_launcher))
                    .into(imgCard)

            imgCard.setOnClickListener { callback?.onMediaClick() }
        }

        return layoutView
    }

}