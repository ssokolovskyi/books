package com.ssokolovskyi.books.view.list

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object ExecutorsProvider {
    private val mainThreadExecutor = object : Executor {
        private val mHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }

    fun getMainThreadExecutor(): Executor = mainThreadExecutor
    fun getBackgroundThreadExecutor(): Executor = Executors.newSingleThreadExecutor()
}
