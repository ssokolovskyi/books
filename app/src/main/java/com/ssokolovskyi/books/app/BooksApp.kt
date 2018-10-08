package com.ssokolovskyi.books.app

import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.ssokolovskyi.books.network.NetworkState

class BooksApp : Application() {
    override fun onCreate() {
        super.onCreate()
        registerReceiver(NetworkState, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }
}
