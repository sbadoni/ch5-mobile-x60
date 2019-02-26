package com.crestron.mobile;

import android.app.Application;

import com.crestron.blackbird.mobile.persistence.listener.RxListenerCsSettings;
import com.crestron.blackbird.mobile.projectmanagement.rx.RxListenerProjMgmt;
import com.crestron.mobile.bcip.CIPCSConnectionHandler;
import com.facebook.stetho.Stetho;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a new instance of the interactor and connect to the settings database
        startRxListeners();
        createCSConnectionHandler();

        //Enable profiling tools
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    /**
     * Initialize all Rx listeners in the app.
     */
    private void startRxListeners() {
        RxListenerCsSettings rxListenerCsSettings = new RxListenerCsSettings(getApplicationContext());
        rxListenerCsSettings.startListening();

        RxListenerProjMgmt rxListenerProjMgmt = new RxListenerProjMgmt();
        rxListenerProjMgmt.startListening();
    }

    /**
     * Creates control system connection handler
     */
    private void createCSConnectionHandler() {
        CIPCSConnectionHandler cipcsConnectionHandler = new CIPCSConnectionHandler();
        cipcsConnectionHandler.init();
    }

}
