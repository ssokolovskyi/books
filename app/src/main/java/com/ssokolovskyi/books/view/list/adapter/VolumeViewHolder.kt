package com.ssokolovskyi.books.view.list.adapter

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.ssokolovskyi.books.R
import com.ssokolovskyi.books.view.list.VolumeListItem

class VolumeViewHolder(view: View) : ViewHolder(view) {

    companion object {
        fun create(parent: ViewGroup): VolumeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.volume_list_item, parent, false)
            return VolumeViewHolder(view)
        }
    }

    private var title = view.findViewById<TextView>(R.id.title)
    private var subtitle = view.findViewById<TextView>(R.id.subtitle)
    private var icon = view.findViewById<ImageView>(R.id.icon)

    fun bind(volume: VolumeListItem, onItemSelected: (String) -> Unit) {
        title.text = volume.title
        subtitle.text = volume.authors
        volume.iconUrl?.let { setImageUrl(it) }
        itemView.setOnClickListener { onItemSelected(volume.id) }
    }

    private fun setImageUrl(url: String) {
        Picasso
            .get()
            .load(url)
            .resize(icon.layoutParams.width, icon.layoutParams.height)
            .centerCrop()
            .into(icon)
    }
}
