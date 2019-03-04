package com.crestron.ch5.webui;

import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * <h1>SystemWebChromeClient class </h1>
 * <p>
 * Intercepts chrome state events from web browser
 *
 * @author Colm Coady
 * @version 1.0
 */

public class SystemWebChromeClient extends WebChromeClient {

    static String TAG = SystemWebChromeClient.class.getSimpleName();

    @Override
    public void onProgressChanged(WebView view, int newProgress){
        Log.d(TAG, "Page loading : " + newProgress + "%");

        if(newProgress == 100){
            // Page loading finish
            Log.d(TAG, "Page Loaded.");

        }
    }
}
