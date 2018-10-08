package com.ssokolovskyi.books.view.list

import android.arch.paging.PagedList
import com.ssokolovskyi.books.network.BooksService
import com.ssokolovskyi.books.network.NetworkAvailabilityProvider
import io.reactivex.disposables.CompositeDisposable

class PagedListProvider(private val booksService: BooksService,
                        private val networkAvailabilityProvider: NetworkAvailabilityProvider,
                        private val dataConverter: VolumesListDataConverter) {

    companion object {
        private const val PAGE_SIZE = 40
        private val Config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(false)
            .build()
    }

    private val pagingCompositeDisposable = CompositeDisposable()

    fun getPagedList(search: String): PagedList<VolumeListItem> {
        pagingCompositeDisposable.clear()
        val dataSource = VolumesDataSource(
            pagingCompositeDisposable,
            booksService,
            search,
            networkAvailabilityProvider,
            dataConverter
        )
        return PagedList.Builder(dataSource, Config)
            .setNotifyExecutor(ExecutorsProvider.getMainThreadExecutor())
            .setFetchExecutor(ExecutorsProvider.getBackgroundThreadExecutor())
            .build()
    }

    fun clear() {
        pagingCompositeDisposable.dispose()
    }
}
