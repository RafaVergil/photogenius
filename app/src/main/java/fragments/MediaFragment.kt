package fragments

import adapters.MediaPagerAdapter
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.VideoView
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
        return inflater.inflate(R.layout.fragment_fs_media, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(context != null && media != null) {

            val imgCard = view.findViewById(R.id.imgCard) as ImageView
            val videoCard = view.findViewById(R.id.videoCard) as VideoView
            val imgVideoPreview = view.findViewById(R.id.imgVideoPreview) as ImageView
            val rlOverlay = view.findViewById(R.id.rlOverlay) as RelativeLayout

            when(media!!.type.toLowerCase()){
                CONSTANTS.KEY_INSTAGRAM_MEDIA_TOKEN_TYPE_IMAGE -> {
                    videoCard.visibility = View.GONE
                    rlOverlay.visibility = View.GONE
                    imgCard.visibility = View.VISIBLE

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
                }

                CONSTANTS.KEY_INSTAGRAM_MEDIA_TOKEN_TYPE_VIDEO -> {

                    rlOverlay.visibility = View.VISIBLE
                    videoCard.visibility = View.GONE
                    imgCard.visibility = View.GONE

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ViewCompat.setTransitionName(videoCard, media!!.id)
                    }

                    Glide.with(context!!)
                            .load(media!!.images.standard_resolution.url)
                            .apply(RequestOptions()
                                    .placeholder(R.drawable.ic_image))
                            .into(imgVideoPreview)

                    videoCard.setVideoURI(
                            Uri.parse(media!!.videos.standard_resolution.url))

                    videoCard.seekTo(1)
                    videoCard.setZOrderOnTop(true)

                    videoCard.setOnCompletionListener { mediaPlayer ->
                        rlOverlay.visibility = View.VISIBLE
                        videoCard.visibility = View.GONE
                    }
                    videoCard.setOnTouchListener { _, _ ->
                        rlOverlay.visibility = View.GONE
                        videoCard.visibility = View.VISIBLE
                        videoCard.start()
                        false
                    }

                    rlOverlay.setOnClickListener{
                        rlOverlay.visibility = View.GONE
                        videoCard.visibility = View.VISIBLE
                        videoCard.start()
                    }

                }
            }
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