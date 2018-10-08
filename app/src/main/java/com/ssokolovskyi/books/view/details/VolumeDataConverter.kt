package com.ssokolovskyi.books.view.details

import android.content.Context
import com.ssokolovskyi.books.R
import com.ssokolovskyi.books.network.Volume

class VolumeDataConverter(private val context: Context) {

    fun convertData(volume: Volume): VolumeViewData {
        val info = volume.volumeInfo
        return VolumeViewData(
            title = info.title,
            authors = getAuthors(volume),
            iconUrl = info.imageLinks?.thumbnail,
            publisher = context.getString(R.string.publisher, info.publisher),
            publishedDate = context.getString(R.string.published_date, info.publishedDate),
            pageCount = context.getString(R.string.pages, info.pageCount),
            language = context.getString(R.string.language, info.language),
            previewLink = info.previewLink
        )
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
