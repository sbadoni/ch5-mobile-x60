package com.crestron.ch5.webui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crestron.ch5.MainActivity;


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
    boolean loadingFinished = true;
    boolean redirect = false;
    private Context mContext;

    public SystemWebViewClient(Context context) {
        mContext = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(
            WebView view, WebResourceRequest request) {
        if (!loadingFinished) {
            redirect = true;
        }

        loadingFinished = false;
        view.loadUrl(request.getUrl().toString());
        return true;
    }

    @Override
    public void onPageStarted(
            WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        loadingFinished = false;
        //SHOW LOADING IF IT ISNT ALREADY VISIBLE
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (!redirect) {
            loadingFinished = true;
        }

        if (loadingFinished && !redirect) {
            //HIDE LOADING IT HAS FINISHED
            Log.d( "","onPageFinished: loadingFinished");
            MainActivity activity = (MainActivity) mContext;
            activity.onProjectLoaded();
        } else {
            redirect = false;
        }
    }
}
