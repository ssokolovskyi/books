package com.ssokolovskyi.books.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object NetworkState : BroadcastReceiver(), NetworkChanges {

    private val subject = PublishSubject.create<Unit>()

    override fun onReceive(context: Context?, intent: Intent?) {
        subject.onNext(Unit)
    }

    override fun networkChanges(): Observable<Unit> {
        return subject
    }
}

interface NetworkChanges {
    fun networkChanges(): Observable<Unit>
}
