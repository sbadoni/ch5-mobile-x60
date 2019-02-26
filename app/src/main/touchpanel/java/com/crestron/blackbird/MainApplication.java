package com.crestron.blackbird;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a new instance of the interactor and connect to the settings database
        startRxListeners();
        createCSConnectionHandler();
        //Enable profiling tools
        if (com.crestron.mobile.bcip.BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    /**
     * Initialize all Rx listeners in the app.
     */
    private void startRxListeners() {

    }

    /**
     * Creates control system connection handler
     */
    private void createCSConnectionHandler() {
        //CIPCSConnectionHandler cipcsConnectionHandler = new CIPCSConnectionHandler(getApplicationContext());
        //cipcsConnectionHandler.init();
    }

}
