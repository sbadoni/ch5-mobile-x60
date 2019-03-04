package com.crestron.ch5.webui;

import android.content.Context;
import android.webkit.JavascriptInterface;

abstract class AppJs {

    private static final String TAG = AppJSInterface.class.getSimpleName();

    protected UIEventHandler mUIEventHandler;
    private Context mContext = null;
    // Instantiate the interface and set the context
    public AppJs(Context context) {
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
