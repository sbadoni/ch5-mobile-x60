package com.crestron.mobile.webui;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * <h1>SystemWebChromeClient class </h1>
 * <p>
 * Handles JavaScript events
 *
 * @author Colm Coady
 * @version 1.0
 */

public class SystemWebViewClient extends WebViewClient {

    static String TAG = SystemWebViewClient.class.getSimpleName();

    @Override
    public void onPageFinished (WebView view, String url) {
        super.onPageFinished(view, url);
    }
}
