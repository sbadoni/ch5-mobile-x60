package com.crestron.common.mobile

import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for RxBus functionality.
 */
class RxBusTest {
    private val friendlySender_1 = FriendlySender1()
    private val friendlySender_2 = FriendlySender2()
    private var friendlyListener_1 = FriendlyListener1()
    private var friendlyListener_2 = FriendlyListener2()

    @Test
    fun `Senders should send correct events to listeners`() {
        /*Given*/
        /*When*/
        friendlySender_1.queryLightingScenes()
        friendlySender_2.queryLightingScenes()

        /*Then*/
        assertTrue(friendlyListener_1.setProgressIndicator)
        assertTrue(friendlyListener_1.receiveLightingScenes)
        assertFalse(friendlyListener_1.showError)

        assertTrue(friendlyListener_2.setProgressIndicator)
        assertTrue(friendlyListener_2.receiveLightingScenes)
        assertFalse(friendlyListener_2.showError)
    }

    @Test
    fun `Should unsubscribe one listener and not the other`() {
        /*Given*/
        friendlySender_1.queryLightingScenes()

        /*When*/
        friendlyListener_1.simulateOnDestroy()
        friendlySender_2.queryLightingScenes()

        /*Then*/
        assertFalse(friendlyListener_1.setProgressIndicator)
        assertFalse(friendlyListener_1.receiveLightingScenes)
        assertTrue(friendlyListener_1.disposable.isDisposed)

        assertTrue(friendlyListener_2.setProgressIndicator)
        assertTrue(friendlyListener_2.receiveLightingScenes)
        assertFalse(friendlyListener_2.disposable.isDisposed)
    }

    @Test
    fun `Should unsubscribe all active listeners`() {
        /*Given*/
        friendlySender_1.queryLightingScenes()
        friendlySender_2.queryLightingScenes()

        /*When*/
        friendlyListener_1.simulateOnDestroy()
        friendlyListener_2.simulateOnDestroy()
        friendlySender_1.queryLightingScenes()
        friendlySender_2.queryLightingScenes()

        /*Then*/
        assertFalse(friendlyListener_1.setProgressIndicator)
        assertFalse(friendlyListener_1.receiveLightingScenes)
        assertTrue(friendlyListener_1.disposable.isDisposed)

        assertFalse(friendlyListener_2.setProgressIndicator)
        assertFalse(friendlyListener_2.receiveLightingScenes)
        assertTrue(friendlyListener_2.disposable.isDisposed)
    }

    @Test
    fun `Should be able to re-subscrube after unsubscribing`() {
        /*Given*/
        friendlyListener_1.simulateOnDestroy()
        friendlyListener_2.simulateOnDestroy()

        /*When*/
        friendlyListener_1 = FriendlyListener1()
        friendlySender_1.queryLightingScenes()
        friendlyListener_2 = FriendlyListener2()
        friendlySender_2.queryLightingScenes()

        assertTrue(friendlyListener_1.setProgressIndicator)
        assertTrue(friendlyListener_1.receiveLightingScenes)

        assertTrue(friendlyListener_2.setProgressIndicator)
        assertTrue(friendlyListener_2.receiveLightingScenes)
    }

}