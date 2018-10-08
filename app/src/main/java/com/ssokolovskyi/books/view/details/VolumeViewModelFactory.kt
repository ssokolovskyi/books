package com.ssokolovskyi.books.view.details

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider.NewInstanceFactory

@Suppress("UNCHECKED_CAST")
class VolumeViewModelFactory(private val id: String,
                             private val application: Application) : NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VolumeViewModel(id, application) as T
    }
}
