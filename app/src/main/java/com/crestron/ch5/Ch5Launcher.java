package com.crestron.ch5;

import android.app.Application;

import com.facebook.stetho.Stetho;

public abstract  class Ch5Launcher extends Application{

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

    abstract void  startRxListeners();
    abstract void  createCSConnectionHandler();
}
