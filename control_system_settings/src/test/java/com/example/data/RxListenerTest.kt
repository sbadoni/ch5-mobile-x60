package com.example.data

import android.content.Context
import com.crestron.mobile.cssettings.model.ControlSystemEntry
import com.crestron.blackbird.mobile.persistence.listener.RxListenerCsSettings
import com.crestron.mobile.android.common.RxBus
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class RxListenerTest {
    private lateinit var rxListener: RxListenerCsSettings

    private val appContext: Context = RuntimeEnvironment.application

    @Before
    fun setUp() {
        rxListener = Mockito.spy(RxListenerCsSettings(appContext))
        rxListener.startListening()
    }

    @Test
    fun `Should receive proper use case for request`() {
        val controlSystemEntry = ControlSystemEntry()
        controlSystemEntry.friendlyName = "TE"
        controlSystemEntry.hostName1 = "Igor's hostname"
        val list = listOf(controlSystemEntry)
        val useCase = com.crestron.mobile.cssettings.CSPersistenceCreateControlSystemEntryUseCase()
        val request = useCase.Request(list)

        RxBus.send(request)
        val argumentCaptor = ArgumentCaptor.forClass(ControlSystemEntry::class.java)
        Mockito.verify(rxListener).createEntry(argumentCaptor.capture())
    }

    @Test
    fun `Should ignore unrelated use case`() {
        val bogusUseCase = BogusUseCase("some bogy")
        val request = bogusUseCase

        RxBus.send(request);
        Mockito.verify(rxListener, never()).createEntry(ArgumentMatchers.anyObject())
    }

}