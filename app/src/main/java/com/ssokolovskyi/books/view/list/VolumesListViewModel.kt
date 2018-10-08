package com.ssokolovskyi.books.view.list

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import com.ssokolovskyi.books.network.BooksService
import com.ssokolovskyi.books.network.NetworkAvailabilityProvider
import com.ssokolovskyi.books.view.list.SearchData.EmptySearch
import com.ssokolovskyi.books.view.list.SearchData.SearchValue
import com.ssokolovskyi.books.view.list.ViewData.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class VolumesListViewModel(application: Application)
    : AndroidViewModel(application) {

    private val booksService = BooksService.getService()
    private val networkAvailabilityProvider = NetworkAvailabilityProvider(getApplication())
    private val navigator = VolumesListNavigator(getApplication())
    private val dataConverter = VolumesListDataConverter(getApplication())
    private val pagedListProvider = PagedListProvider(booksService, networkAvailabilityProvider, dataConverter)

    private var viewData = MutableLiveData<ViewData>()
    private val searchSubject = BehaviorSubject.create<SearchData>()
    private val compositeDisposable = CompositeDisposable()

    init {
        val searchChanges = searchSubject
            .distinctUntilChanged()
            .debounce(350L, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())

        compositeDisposable.add(
            searchChanges
                .switchMap { search ->
                    networkAvailabilityProvider
                        .hasConnection()
                        .observeOn(Schedulers.io())
                        .takeUntil { it }.map { search to it }
                }
                .map { (search, network) ->
                    when (search) {
                        EmptySearch -> NoData
                        is SearchValue -> {
                            if (network) ListData(pagedListProvider.getPagedList(search.input))
                            else NoNetwork
                        }
                    }
                }
                .startWith(NoData)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewData.value = it }
        )
    }

    fun onSearch(searchData: SearchData) {
        searchSubject.onNext(searchData)
    }

    fun getViewData(): LiveData<ViewData> {
        return viewData
    }

    fun onItemClicked(id: String) {
        navigator.navigateToVolume(id)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
        pagedListProvider.clear()
    }
}

sealed class ViewData {
    data class ListData(val pagedList: PagedList<VolumeListItem>) : ViewData()
    object NoData : ViewData()
    object NoNetwork : ViewData()
}

sealed class SearchData {
    object EmptySearch : SearchData()
    data class SearchValue(val input: String) : SearchData()
}

data class VolumeListItem(val id: String,
                          val title: String,
                          val authors: String?,
                          val iconUrl: String?)
