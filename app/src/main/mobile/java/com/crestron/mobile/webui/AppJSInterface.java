package com.crestron.mobile.webui;

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

public class AppJSInterface {

    private static final String TAG = AppJSInterface.class.getSimpleName();

    private UIEventHandler mUIEventHandler;

    // Instantiate the interface and set the context
    public AppJSInterface(Context context) {
        mUIEventHandler = new UIEventHandler(context);
        mUIEventHandler.init();
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

    @JavascriptInterface
    public void bridgeSendIntegerToNative(String signalName, int value) {

        if (signalName.contains("Csig.")) {
            String intSignalName = removeCsigStringFromSignalName(signalName);
            mUIEventHandler.sendIntegerReservedUseCase(intSignalName, value);
        } else {
            mUIEventHandler.sendIntegerUseCase(signalName, value);
        }

    }

    @JavascriptInterface
    public void bridgeSendStringToNative(String signalName, String value) {

        if (signalName.contains("Csig.")) {
            String stringSignalName = removeCsigStringFromSignalName(signalName);
            mUIEventHandler.sendStringReservedUseCase(stringSignalName, value);
        } else {
            mUIEventHandler.sendStringUseCase(signalName, value);
        }

    }

    @JavascriptInterface
    public void bridgeSendBooleanToNative(String signalName, boolean value) {

        if (signalName.contains("Csig.")) {
            String boolSignalName = removeCsigStringFromSignalName(signalName);
            mUIEventHandler.sendBooleanReservedUseCase(boolSignalName, value);

        } else {
            mUIEventHandler.sendBoolUseCase(signalName, value);
        }

    }


    @JavascriptInterface
    public void bridgeSendObjectToNative(String signalName, String jsonEncodedObject) {

        mUIEventHandler.sendObjectUseCase(signalName, jsonEncodedObject);
    }

    @JavascriptInterface
    public void bridgeSendArrayToNative(String jsonEncodedArray) {

    }

    public String removeCsigStringFromSignalName(String signalName) {
        String signalNameWithoutCsig = signalName.replaceAll("Csig.", "");
        return signalNameWithoutCsig;
    }


}
