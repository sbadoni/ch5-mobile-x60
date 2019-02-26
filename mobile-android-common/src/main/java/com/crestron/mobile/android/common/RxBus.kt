package com.crestron.mobile.android.common

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

// Use object so we have a singleton instance
object RxBus {
    val publisher = BehaviorSubject.create<Any>().toSerialized()

    fun send(event: Any) {
        publisher.onNext(event)
    }

    // Listen should return an Observable and not the publisher.
    // Using ofType we filter only events that match that class type.
    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)

}