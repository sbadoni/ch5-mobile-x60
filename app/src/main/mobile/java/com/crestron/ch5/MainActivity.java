package com.crestron.ch5;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;


/**
 * <h1>TouchPanelDashboard class </h1>
 * <p>
 * Activity that contains the web view
 *
 * @author Colm Coady
 * @version 1.0
 */

public class MainActivity extends HomeActivity {

    static String TAG = HomeActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Sameer***************************** MainActivity");
        // TODO test transparency for user project
        // Get the Android assets folder path
        String file = "file:android_asset/setup/www/index.html";
        // Render the HTML file on WebView
        if (!Features.demo) {
            mWebView.setVisibility(View.VISIBLE);
        }
        mWebView.loadUrl(file);

        reportFullyDrawn();
    }
}
