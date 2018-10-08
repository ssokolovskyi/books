package com.ssokolovskyi.books.view.list.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil.ItemCallback
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.ViewGroup
import com.ssokolovskyi.books.view.list.VolumeListItem

class VolumesListAdapter(private val onItemSelected: (String) -> Unit)
    : PagedListAdapter<VolumeListItem, ViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : ItemCallback<VolumeListItem>() {
            override fun areItemsTheSame(oldItem: VolumeListItem, newItem: VolumeListItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: VolumeListItem, newItem: VolumeListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VolumeViewHolder.create(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as VolumeViewHolder).bind(getItem(position)!!, onItemSelected)
}
