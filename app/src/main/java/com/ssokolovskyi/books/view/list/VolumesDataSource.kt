package com.ssokolovskyi.books.view.list

import android.arch.paging.PageKeyedDataSource
import com.ssokolovskyi.books.network.BooksService
import com.ssokolovskyi.books.network.NetworkAvailabilityProvider
import com.ssokolovskyi.books.network.VolumesResponse
import com.ssokolovskyi.books.view.list.VolumesDataSource.Data.FetchedData
import com.ssokolovskyi.books.view.list.VolumesDataSource.Data.NoInternet
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.io.InterruptedIOException

class VolumesDataSource(private val compositeDisposable: CompositeDisposable,
                        private val booksService: BooksService,
                        private val search: String,
                        private val networkAvailabilityProvider: NetworkAvailabilityProvider,
                        private val dataConverter: VolumesListDataConverter) : PageKeyedDataSource<Int, VolumeListItem>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, VolumeListItem>) {
        compositeDisposable.add(
            getPageData(params.requestedLoadSize, 0)
                .subscribeWithErrorHandler { data ->
                    when (data) {
                        NoInternet -> callback.onResult(emptyList(), null, null)
                        is FetchedData -> {
                            val items = data.response.items ?: emptyList()
                            val nextPage = if (items.size == params.requestedLoadSize) 1 else null
                            callback.onResult(dataConverter.convertData(items), null, nextPage)
                        }
                    }
                }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, VolumeListItem>) {
        compositeDisposable.add(
            getPageData(params.requestedLoadSize, params.key * params.requestedLoadSize)
                .subscribeWithErrorHandler { data ->
                    when (data) {
                        NoInternet -> callback.onResult(emptyList(), null)
                        is FetchedData -> {
                            val items = data.response.items ?: emptyList()
                            val nextPage = if (items.size == params.requestedLoadSize) params.key + 1 else null
                            callback.onResult(dataConverter.convertData(items), nextPage)
                        }
                    }
                }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, VolumeListItem>) {
    }

    private fun getPageData(maxResult: Int, startIndex: Int): Observable<Data> {
        return networkAvailabilityProvider
            .hasConnection()
            .switchMap { network ->
                when (network) {
                    true -> {
                        booksService
                            .getVolumes(search, maxResult, startIndex)
                            .map { FetchedData(it) }
                            .toObservable()
                    }
                    false -> Observable.just(NoInternet)
                }
            }
            .take(1)
    }

    private fun <T> Observable<T>.subscribeWithErrorHandler(action: (T) -> Unit): Disposable {
        return subscribe(
            { action(it) },
            { error ->
                when (error) {
                    is InterruptedIOException -> Unit //unsubscribe interrupts the thread and throws this error.
                    else -> throw error
                }
            }
        )
    }

    private sealed class Data {
        object NoInternet : Data()
        class FetchedData(val response: VolumesResponse) : Data()
    }
}
