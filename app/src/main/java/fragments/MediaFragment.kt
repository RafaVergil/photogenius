package fragments

import adapters.MediaPagerAdapter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import br.com.rafaelverginelli.photogenius.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import models.MediaModel
import uk.co.senab.photoview.PhotoViewAttacher
import utils.CONSTANTS


class MediaFragment : Fragment(){

    private var media: MediaModel? = null
    private var callback: MediaPagerAdapter.IMediaCallback? = null
    private var photoViewAttacher: PhotoViewAttacher? = null

    companion object {
        fun init(mediaModel: MediaModel, callback: MediaPagerAdapter.IMediaCallback)
                : MediaFragment {

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

        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater
                    .from(context)
                    .inflateTransition(android.R.transition.move)
        }
        sharedElementReturnTransition = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layoutView = inflater.inflate(R.layout.fragment_fs_media, container,
                false)

        return layoutView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(context != null && media != null) {
//            val zoomLayout = view.findViewById(R.id.zoomLayout) as ZoomLayout
//            zoomLayout.zoomTo(1f, false)
            val imgCard = view.findViewById(R.id.imgCard) as ImageView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewCompat.setTransitionName(imgCard, media!!.id)
            }

            Glide.with(context!!)
                    .load(media!!.images.standard_resolution.url)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.ic_image))
                    .listener(object: RequestListener<Drawable>{
                        override fun onLoadFailed(e: GlideException?, model: Any?,
                                                  target: Target<Drawable>?,
                                                  isFirstResource: Boolean): Boolean {

                            startPostponedEnterTransition()

                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target:
                        Target<Drawable>?, dataSource: DataSource?,
                                                     isFirstResource: Boolean): Boolean {

                            if (photoViewAttacher == null) {
                                photoViewAttacher = PhotoViewAttacher(imgCard)
                            }

                            photoViewAttacher!!.scale = 1f
                            photoViewAttacher!!.update()

                            startPostponedEnterTransition()

                            return false
                        }

                    })
                    .into(imgCard)

            imgCard.setOnClickListener { callback?.onMediaClick() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (photoViewAttacher != null) {
            if (photoViewAttacher!!.imageView != null) {
                photoViewAttacher!!.imageView.setImageBitmap(null)
                photoViewAttacher!!.imageView.background = null
                photoViewAttacher!!.cleanup()
                photoViewAttacher = null
            }
        }
    }

}