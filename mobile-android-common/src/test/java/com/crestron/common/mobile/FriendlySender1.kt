package com.crestron.common.mobile

import com.crestron.common.mobile.mocks.MockPyngSubject
import com.crestron.mobile.android.common.RxBus
import java.text.SimpleDateFormat
import java.util.*

/**
 * A friendly implementation of a sender that sends events to RxBus.
 */
class FriendlySender1 {

    /**
     * Query the available lighting scenes from Pyng Hub.
     *
     * @param lifecycle Any - a reference object that will be unregistered from the bus when
     * application is destroyed.
     */
    fun queryLightingScenes() {
        val timeStamp = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date())
        System.err.println("sent event from sender1 on thread: " + Thread.currentThread().id + " at " + timeStamp);
        RxBus.send(MockPyngSubject(MockPyngSubject.SET_PROGRESS_INDICATOR, "fetching lighting scenes in sender1..."));
        simulateReceiveLightingScenes()
    }

    private fun simulateReceiveLightingScenes() {
        RxBus.send(MockPyngSubject(MockPyngSubject.RECEIVE_LIGHTING_SCENES, "scenes from sender1"))
    }

}