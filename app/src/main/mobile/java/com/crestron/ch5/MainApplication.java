package com.crestron.ch5;

import android.app.Application;

import com.crestron.blackbird.mobile.persistence.listener.RxListenerCsSettings;
import com.crestron.blackbird.mobile.projectmanagement.rx.RxListenerProjMgmt;
import com.crestron.mobile.bcip.CIPCSConnectionHandler;
import com.facebook.stetho.Stetho;

public class MainApplication extends Ch5Launcher {

    /**
     * Initialize all Rx listeners in the app.
     */
    protected void startRxListeners() {
        RxListenerCsSettings rxListenerCsSettings = new RxListenerCsSettings(getApplicationContext());
        rxListenerCsSettings.startListening();

        RxListenerProjMgmt rxListenerProjMgmt = new RxListenerProjMgmt();
        rxListenerProjMgmt.startListening();
    }

    /**
     * Creates control system connection handler
     */
    protected void createCSConnectionHandler() {
        CIPCSConnectionHandler cipcsConnectionHandler = new CIPCSConnectionHandler();
        cipcsConnectionHandler.init();
    }

}
