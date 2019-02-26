package com.crestron.mobile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.crestron.mobile.webui.AppJSInterface;
import com.crestron.mobile.webui.SystemWebChromeClient;
import com.crestron.mobile.webui.SystemWebViewClient;

/**
 * <h1>MainActivity class </h1>
 * <p>
 * Activity that contains the web view
 *
 * @author Colm Coady
 * @version 1.0
 */

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private ImageView mImageView;
    private ProgressDialog mProgDialog;
    static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_main);


        mWebView = findViewById(R.id.web_view);
        if (!Features.demo) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        //mImageView = (ImageView) findViewById(R.id.imageView);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // TODO test transparency for user project
        //mWebView.setBackgroundColor(Color.TRANSPARENT);
        // Get the Android assets folder path
        String file = "file:android_asset/setup/www/index.html";

        if (!Features.demo) {
            mWebView.setWebViewClient(new SystemWebViewClient());
        }
        mWebView.setWebChromeClient(new SystemWebChromeClient());
        mWebView.addJavascriptInterface(new AppJSInterface(this), "JSInterface");

        // Render the HTML file on WebView

        if (!Features.demo) {
            mWebView.setVisibility(View.VISIBLE);
        }
        mWebView.loadUrl(file);

        reportFullyDrawn();
    }

    protected void onDestroy() {
        super.onDestroy();
        /*if (isFinishing()) {

        }*/
    }

    /**
     * Shows a progress dialog.
     * NOTE: maybe removed from later versions of this file
     *
     * @param strProgressMsg message to display
     */
    public void showProgressMsg(String strProgressMsg)
    {
        try {
            runOnUiThread(() -> {
                mProgDialog.setMessage(strProgressMsg);
                mProgDialog.show();
            });
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Dismisses progresss dialog
     *
     */
    public void dismissProgressMsg()
    {
        try {
            runOnUiThread(() -> {
                if (mProgDialog.isShowing()) {
                    mProgDialog.dismiss();
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Load project html file in web view
     *
     * @param strProjectPath location of html file
     */
    public void loadDownloadedProject(final String strProjectPath) {
        try {
            runOnUiThread(() -> {
                mWebView.loadUrl(strProjectPath);
            });
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Executes javascript on web view
     *
     * @param strJavascript javascript function to execute
     */
    public void sendToWebView(final String strJavascript) {
        try {
            runOnUiThread(() -> {
                    mWebView.evaluateJavascript("javascript: "+strJavascript , null);
            });
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }
}
