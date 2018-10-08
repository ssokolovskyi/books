package com.ssokolovskyi.books.network

import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.Observable

class NetworkAvailabilityProvider(private val context: Context) {

    fun hasConnection(): Observable<Boolean> = NetworkState
        .networkChanges()
        .map { isNetworkAvailable() }
        .startWith(isNetworkAvailable())
        .distinctUntilChanged()

    private fun isNetworkAvailable() = context
        .getSystemService(Context.CONNECTIVITY_SERVICE)
        .let { it as ConnectivityManager }
        .activeNetworkInfo
        ?.isConnected ?: false
}
