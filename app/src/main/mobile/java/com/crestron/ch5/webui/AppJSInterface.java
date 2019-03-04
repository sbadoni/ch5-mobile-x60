package com.crestron.ch5.webui;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.crestron.cip.BcipConnectionInfo;

/**
 * <h1>AppJSInterface class </h1>
 * <p>
 * Receives JavaScript calls from web browser
 *
 * @author Colm Coady
 * @version 1.0
 */

public class AppJSInterface extends AppJs{
    private static final String TAG = AppJSInterface.class.getSimpleName();
    public AppJSInterface(Context context) {
        super(context);
    }

    @JavascriptInterface
    public void getControlSystems(String jsonStr) {
        Log.d(TAG, Thread.currentThread().getId() + ": getControlSystems");
        mUIEventHandler.getControlSystemEntries();
    }

    @JavascriptInterface
    public void connectToControlSystem(String jsonStr) {
        Log.d(TAG, Thread.currentThread().getId() + ": connectToControlSystem");
        mUIEventHandler.connectToControlSystem(jsonStr);
    }

    @JavascriptInterface
    public void saveControlSystemEntry(String jsonStr) {
        Log.d(TAG, Thread.currentThread().getId() + ": saveControlSystemEntry");
        mUIEventHandler.saveControlSystemEntry(jsonStr);
    }

    @JavascriptInterface
    public void deleteControlSystemEntry(String jsonStr) {
        Log.d(TAG, Thread.currentThread().getId() + ": deleteControlSystemEntry");
        mUIEventHandler.deleteControlSystemEntry(jsonStr);
    }
}
