package adapters

import android.app.Activity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.rafaelverginelli.photogenius.R
import models.TagModel

class InstagramTagAdapter(val data: List<TagModel>, val context: Activity,
                          private val callback: ITagCallback) :
        RecyclerView.Adapter<TagHolder>() {

    interface ITagCallback {
        fun onTagClick(tag: TagModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TagHolder {
        return TagHolder(LayoutInflater.from(context)
                .inflate(R.layout.tag_view, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TagHolder, position: Int) {
        holder.txtTag.text = data[position].name
        holder.cardView.setOnClickListener{ callback.onTagClick(data[position]) }
    }

}

class TagHolder(view: View) : RecyclerView.ViewHolder(view) {
    val txtTag: TextView = view.findViewById(R.id.txtTag) as TextView
    val cardView: CardView = view.findViewById(R.id.cardView) as CardView
}