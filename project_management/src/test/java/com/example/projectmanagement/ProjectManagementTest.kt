package com.example.projectmanagement

import android.content.Context
import com.crestron.blackbird.mobile.projectmanagement.rx.RxListenerProjMgmt
import com.crestron.mobile.android.common.RxBus
import com.crestron.mobile.cssettings.model.ControlSystemEntry
import com.crestron.mobile.projectmanagement.DownloadProjectUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Test acceptance criteria for project management/download feature.
 */
@RunWith(RobolectricTestRunner::class)
class ProjectManagementTest {
    private lateinit var rxListener: RxListenerProjMgmt;

    private val context: Context = RuntimeEnvironment.application

    @Before
    fun setUp() {
        rxListener = Mockito.spy(RxListenerProjMgmt())
        rxListener.startListening()
    }

    @Test
    fun `Should listen to proper use cases`() {
        //Download project from CS
        /*Given*/
        val downloadProjectUseCase = DownloadProjectUseCase().Request()

        /*When*/
        RxBus.send(downloadProjectUseCase)

        /*Then*/
        val argumentCaptor = ArgumentCaptor.forClass(ControlSystemEntry::class.java)
        //Mockito.verify(rxListener).downloadProject(argumentCaptor.capture())
    }

    @Test
    fun `Should clear cached files after a period of time`() {
        throw NotImplementedError("not yet implemented")
    }

    @Test
    fun `Should create separate hash for each zip downloaded`() {
        throw NotImplementedError("not yet implemented")
    }
}