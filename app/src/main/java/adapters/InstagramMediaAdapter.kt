package adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.com.rafaelverginelli.photogenius.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import models.MediaModel

class InstagramMediaAdapter(val data: List<MediaModel>, val context: Activity,
                            private val callback: IMediaCallback) :
        RecyclerView.Adapter<MediaHolder>() {

    interface IMediaCallback {
        fun onMediaClick(index: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MediaHolder {
        return MediaHolder(LayoutInflater.from(context)
                .inflate(R.layout.card_view, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MediaHolder, position: Int) {

        Glide.with(context)
                .load(data[position].images.standard_resolution.url)
                .apply(RequestOptions()
                        .placeholder(R.drawable.ic_image))
                .into(holder.imgCard)

        holder.txtLikeCount.text = data[position].likes.count.toString()
        holder.txtCommentCount.text = data[position].comments.count.toString()
        holder.imgCard.setOnClickListener { callback!!.onMediaClick(position) }

    }


}

class MediaHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imgCard: ImageView = view.findViewById(R.id.imgCard) as ImageView
    val txtLikeCount: TextView = view.findViewById(R.id.txtLikeCount) as TextView
    val txtCommentCount: TextView = view.findViewById(R.id.txtCommentCount) as TextView
}