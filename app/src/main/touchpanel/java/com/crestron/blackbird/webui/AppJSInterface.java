package com.crestron.blackbird.webui;

import android.content.Context;
import android.webkit.JavascriptInterface;

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
    Context mContext = null;
    private UIEventHandler mUIEventHandler;


    // Instantiate the interface and set the context
    public AppJSInterface(Context context) {
        mContext = context;
    }

    public void init()
    {
        mUIEventHandler = new UIEventHandler(mContext);
        mUIEventHandler.init();
    }

    public void deinit()
    {
        mUIEventHandler.deinit();
    }

    @JavascriptInterface
    public void bridgeSendIntegerToNative(String signalName, int value) {
        if (signalName.startsWith("Csig")) {
            mUIEventHandler.sendIntReservedUseCase(signalName.substring(5), value);
        } else {
            mUIEventHandler.sendIntegerUseCase(signalName, value);
        }
    }

    @JavascriptInterface
    public void bridgeSendStringToNative(String signalName, String value) {
        if (signalName.startsWith("Csig")) {
            mUIEventHandler.sendStrReservedUseCase(signalName.substring(5), value);
        } else {
            mUIEventHandler.sendStringUseCase(signalName, value);
        }
    }

    @JavascriptInterface
    public void bridgeSendBooleanToNative(String signalName, boolean value) {
        if (signalName.startsWith("Csig")) {
            mUIEventHandler.sendBoolReservedUseCase(signalName.substring(5), value);
        } else {
            mUIEventHandler.sendBoolUseCase(signalName, value);
        }
    }

    @JavascriptInterface
    public void bridgeSubscribeBooleanSignalFromNative(String signalName) {
        mUIEventHandler.subscribeBooleanSignal(signalName);
    }

    @JavascriptInterface
    public void bridgeSubscribeIntegerSignalFromNative(String signalName) {
        mUIEventHandler.subscribeIntegerSignal(signalName);
    }

    @JavascriptInterface
    public void bridgeSubscribeStringSignalFromNative(String signalName) {
        mUIEventHandler.subscribeStringSignal(signalName);
    }

    @JavascriptInterface
    public void bridgeSendObjectToNative(String signalName, String jsonEncodedObject) {
        mUIEventHandler.sendObjectUseCase(signalName, jsonEncodedObject);
    }

    @JavascriptInterface
    public void bridgeSendArrayToNative(String jsonEncodedArray) {

    }



}
