package com.crestron.common.mobile

import com.crestron.common.mobile.mocks.MockPyngSubject
import com.crestron.mobile.android.common.RxBus
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.*

class FriendlyListener2 {
    var receiveLightingScenes = false
    var setProgressIndicator = false
    var showError = false
    var disposable: Disposable

    init {
        disposable = subscribeToPyngSubject()
    }

    private fun subscribeToPyngSubject(): Disposable {
        return RxBus.listen(MockPyngSubject::class.java).subscribe({
            val timeStamp = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date())
            System.err.println("received event in listener2 on thread: " + Thread.currentThread().id + " at " + timeStamp);
            when (it.subjectId) {
                MockPyngSubject.RECEIVE_LIGHTING_SCENES -> {
                    receiveLightingScenes = true
                }
                MockPyngSubject.SET_PROGRESS_INDICATOR -> {
                    setProgressIndicator = true
                }
                MockPyngSubject.SHOW_ERROR -> {
                    showError = true
                }
            }
        })
    }

    fun simulateOnDestroy() {
        disposable.dispose()
        receiveLightingScenes = false;
        setProgressIndicator = false;
        showError = false;
    }

}