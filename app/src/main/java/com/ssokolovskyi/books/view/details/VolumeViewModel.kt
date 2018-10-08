package com.ssokolovskyi.books.view.details

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.ssokolovskyi.books.network.BooksService
import com.ssokolovskyi.books.network.NetworkAvailabilityProvider
import com.ssokolovskyi.books.view.details.ViewData.NoNetwork
import com.ssokolovskyi.books.view.details.ViewData.Ready
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class VolumeViewModel(volumeId: String, application: Application)
    : AndroidViewModel(application) {

    private val booksService = BooksService.getService()
    private val networkAvailabilityProvider = NetworkAvailabilityProvider(getApplication())
    private val dataConverter = VolumeDataConverter(getApplication())

    private val viewData = MutableLiveData<ViewData>()
    private val compositeDisposable = CompositeDisposable()

    init {
        compositeDisposable.add(
            networkAvailabilityProvider
                .hasConnection()
                .observeOn(Schedulers.io())
                .takeUntil { it }
                .switchMap { network ->
                    when (network) {
                        true -> {
                            booksService
                                .getVolume(volumeId)
                                .toObservable()
                                .map { Ready(dataConverter.convertData(it)) }
                        }
                        false -> Observable.just(NoNetwork)
                    }

                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response -> viewData.value = response }
        )
    }

    fun getViewData(): LiveData<ViewData> {
        return viewData
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}

sealed class ViewData {
    object NoNetwork : ViewData()
    data class Ready(val volumeData: VolumeViewData) : ViewData()
}

data class VolumeViewData(val title: String,
                          val authors: String?,
                          val iconUrl: String?,
                          val publisher: String,
                          val publishedDate: String,
                          val pageCount: String,
                          val language: String,
                          val previewLink: String)
