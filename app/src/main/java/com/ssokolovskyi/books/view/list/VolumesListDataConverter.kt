package com.ssokolovskyi.books.view.list

import android.content.Context
import com.ssokolovskyi.books.R
import com.ssokolovskyi.books.network.Volume

class VolumesListDataConverter(private val context: Context) {

    fun convertData(volumes: List<Volume>): List<VolumeListItem> {
        return volumes.map {
            VolumeListItem(
                id = it.id,
                title = it.volumeInfo.title,
                authors = getAuthors(it),
                iconUrl = it.volumeInfo.imageLinks?.thumbnail
            )
        }
    }

    private fun getAuthors(volume: Volume): String? {
        return volume
            .volumeInfo
            .authors
            ?.asSequence()
            ?.joinToString()
            ?.let { context.getString(R.string.authors, it) }
    }
}
