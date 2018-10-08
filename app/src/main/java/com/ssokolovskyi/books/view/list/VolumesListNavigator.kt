package com.ssokolovskyi.books.view.list

import android.content.Context
import android.content.Intent
import com.ssokolovskyi.books.view.details.VolumesDetailsActivity

class VolumesListNavigator(private val context: Context) {

    fun navigateToVolume(volumeId: String) {
        val intent = Intent(context, VolumesDetailsActivity::class.java)
            .putExtra(VolumesDetailsActivity.VOLUME_ID_ARG, volumeId)
        context.startActivity(intent)
    }
}
