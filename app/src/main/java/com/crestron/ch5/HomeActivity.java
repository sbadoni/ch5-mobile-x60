package com.crestron.ch5;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.crestron.ch5.webui.AppJSInterface;
import com.crestron.ch5.webui.SystemWebChromeClient;
import com.crestron.ch5.webui.SystemWebViewClient;
import com.crestron.logging.Logger;

public class HomeActivity extends AppCompatActivity {

    protected WebView mWebView;
    protected ProgressDialog mProgDialog;
    static String TAG = HomeActivity.class.getSimpleName();
    private AppJSInterface mAppJSInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgDialog = new ProgressDialog(this);

        Logger.init(this);
        mWebView = findViewById(R.id.web_view);
        Log.d(TAG,"Sameer***************************** HomeActivity "+mWebView);
        if (!Features.demo) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (!Features.demo) {
            mWebView.setWebViewClient(new SystemWebViewClient(this));
        }

        mAppJSInterface = new AppJSInterface(this);
        mWebView.setWebChromeClient(new SystemWebChromeClient());
        mWebView.addJavascriptInterface(mAppJSInterface,  "JSInterface");
        mAppJSInterface.init();



       /* final String formFactor = BuildConfig.FORM_FACTOR;
        if(formFactor.equalsIgnoreCase("mobile")){

        }*/
    }


    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {

        }
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

    public void dismissProgressMsg()
    {
        try {
            runOnUiThread(() -> {
                if (mProgDialog.isShowing()) {
                    mProgDialog.dismiss();
                }
            });
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
        }
    }

    public void loadDownloadedProject(final String strProjectPath) {
        try {
            runOnUiThread(() -> {
                mWebView.loadUrl(strProjectPath);
            });
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
        }
    }

    public void sendToWebView(final String strJavascript) {
        try {
            runOnUiThread(() -> {
                mWebView.evaluateJavascript("javascript: "+strJavascript , null);
            });
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
        }
    }
}
