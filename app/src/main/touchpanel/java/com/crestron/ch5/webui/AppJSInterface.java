package com.crestron.ch5.webui;

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

public class AppJSInterface extends AppJs {

    // Instantiate the interface and set the context
    public AppJSInterface(Context context) {
        super(context);
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
    public void bridgeSendArrayToNative(String jsonEncodedArray) {

    }



}
