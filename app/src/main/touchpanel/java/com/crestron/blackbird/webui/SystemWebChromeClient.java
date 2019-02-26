package com.crestron.blackbird.webui;

import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.crestron.logging.Logger;


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
        Logger.Debug( "Page loading : " + newProgress + "%");

        if(newProgress == 100){
            // Page loading finish
            Logger.Debug( "Page Loaded.");

        }
    }
}
