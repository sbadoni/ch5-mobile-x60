package com.crestron.ch5;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class MainApplication extends Ch5Launcher {
    /**
     * Initialize all Rx listeners in the app.
     */
    protected void startRxListeners() {

    }

    /**
     * Creates control system connection handler
     */
    protected void createCSConnectionHandler() {
        //CIPCSConnectionHandler cipcsConnectionHandler = new CIPCSConnectionHandler(getApplicationContext());
        //cipcsConnectionHandler.init();
    }

}
