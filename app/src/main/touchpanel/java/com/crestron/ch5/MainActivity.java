package com.crestron.ch5;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;


import com.crestron.ch5.Features;
import com.crestron.ch5.HomeActivity;
import com.crestron.ch5.R;
import com.crestron.ch5.webui.AppJSInterface;
import com.crestron.ch5.webui.SystemWebViewClient;
import com.crestron.logging.Logger;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.bcip.CIPConnectToIPLinkUseCase;
import com.crestron.mobile.layout.ProjectReadyUseCase;
import com.crestron.mobile.signal.SignalManager;
import com.crestron.video.panel.VideoManagerRx;

import java.io.File;
import java.io.InputStream;

import mobile.crestron.com.connection_manager.ConnectionMngr;


/**
 * <h1>MainActivity class </h1>
 * <p>
 * Activity that contains the web view
 *
 * @author Colm Coady
 * @version 1.0
 */

public class MainActivity extends HomeActivity {
   // private WebView mWebView;
   // private ImageView mImageView;
   // private ProgressDialog mProgDialog;
    static String TAG = MainActivity.class.getSimpleName();
    String pathToDisplayProject;
    String hardKeyMappingFile;
    int ipLinkPort;
    public static String pathToDisplayProjectKey = "pathToDisplayProject";
    public static String hardKeyMappingFileKey = "hardKeyMappingFile";
    public static String ipLinkPortKey = "ipLinkPort";

    private SignalManager mSignalManager;
    private ConnectionMngr mConnectionMngr;
    private boolean mProjectLoaded = false;
    private boolean mSignalsLoaded = false;

    private VideoManagerRx mVideoManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSignalManager = SignalManager.getInstance();
        mConnectionMngr = new ConnectionMngr(this);
        mConnectionMngr.init();

        Logger.Info("$$$$$$$$$$$$$$$$$$$$$$$$$ MainActivity  started");
        extractIntentExtras();
        Logger.Info("Connecting over iplink");
        startTransportSignal(ipLinkPort,true);

        String file = pathToDisplayProject + "/index.html";

        File indexFile = new File(file);
        Uri projectMainPageUri = Uri.fromFile(indexFile);

        if (!Features.demo) {
            mWebView.setWebViewClient(new SystemWebViewClient(this));
        }

        // Render the HTML file on WebView

        if (!Features.demo) {
            mWebView.setVisibility(View.VISIBLE);
        }

        Logger.Info("Loading project and signals");
        mProgDialog.setMessage("Loading. Please wait.");
        mProgDialog.show();
        mWebView.loadUrl(String.valueOf(projectMainPageUri));

        // to disable text selection
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mWebView.setLongClickable(false);

        loadSignals(pathToDisplayProject);
        reportFullyDrawn();
        mVideoManager = new VideoManagerRx(this);
    }

    /**
     * Extracts intent information for project path, hardkey mapping file and ip link port
     *
     */
    private void extractIntentExtras(){
        Intent mIntent = getIntent();
        if (mIntent != null) {
            pathToDisplayProject = mIntent.getStringExtra("pathToDisplayProject");
            if  (pathToDisplayProject == null) {
                Logger.Debug( "pathToDisplayProject null. Using default value.");
                pathToDisplayProject = "/sdcard/ROMDISK/display";
            }

            hardKeyMappingFile = mIntent.getStringExtra("hardKeyMappingFile");
            if (hardKeyMappingFile == null)
            {
                Logger.Debug( "hardKeyMappingFile null. Using default value.");
                hardKeyMappingFile = "/system/crestron/data/hardkeyMapping";
            }

            ipLinkPort = mIntent.getIntExtra("ipLinkPort", 0);
            if (ipLinkPort == 0) {
                Logger.Debug( "ipLinkPort 0. Using default value.");
                ipLinkPort = 42872;
            }

        }
        else
        {
            Logger.Debug( "No intents. Using default values");
            pathToDisplayProject = "/sdcard/ROMDISK/display";
            hardKeyMappingFile = "/system/crestron/data/hardkeyMapping";
            ipLinkPort = 42872;
        }
        Logger.Info("pathToDisplayProject: " + pathToDisplayProject + " hardKeyMappingFile: " + hardKeyMappingFile + " ipLinkPort: " + ipLinkPort);
    }

    /**
     * Send request to start connection to iplink and CresStore through Signal Transport
     *
     *  @param ipLinkPort ip link port
     *  @param enableIplinkLogging enable ip link logging
     */
    private void startTransportSignal(int ipLinkPort, boolean enableIplinkLogging) {

        CIPConnectToIPLinkUseCase.Request connectReq = new CIPConnectToIPLinkUseCase().new Request();
        if (ipLinkPort > 0)
            connectReq.setIPLinkPort(ipLinkPort);
        RxBus.INSTANCE.send(connectReq);

    }

    public void  handleVideoCommand(String action){
        System.out.println("VIDEO ======================== " + action);
    }

    /**
     * Parses json file which contains user defined signals.
     * Signals are load into the relevant maps.
     *
     *  @param pathToDisplayProject path to signal json file
     *
     */
    private void loadSignals(String pathToDisplayProject)
    {
        // Lambda Runnable
        Runnable signalLoadTask = () ->
        {
            String userJSONFilePath = pathToDisplayProject + "/config/signalJoinMap.json";

            InputStream reservedJSONFile = getResources().openRawResource(R.raw.reservedjoins);

            File userJSONFile = new File(userJSONFilePath);

            mSignalManager.init(userJSONFile, reservedJSONFile);

            Logger.Debug( "onSignalsLoaded");

            try {
                runOnUiThread(() -> {
                    mSignalsLoaded = true;

                    if (mSignalsLoaded && mProjectLoaded)
                    {
                        if (mProgDialog.isShowing()) {
                            mProgDialog.dismiss();
                        }
                        Logger.Info("Project and signals loaded");
                        ProjectReadyUseCase projectReadyUseCase = new ProjectReadyUseCase();
                        RxBus.INSTANCE.send(projectReadyUseCase);
                    }
                });
            } catch (Exception e) {
                Logger.Error( "Error e: " + e.toString());
            }
        };

        try {
            Thread t = new Thread(signalLoadTask);

            t.start();
        }
        catch (Exception e)
        {
            Logger.Error( "Error e: " + e.toString());
        }
    }

    /**
     * Called by the WebViewClient after the html project has fully loaded.
     *
     */
    public void onProjectLoaded()
    {
        try {
            runOnUiThread(() -> {

                Logger.Debug( "onProjectLoaded");
                mProjectLoaded = true;

                if (mSignalsLoaded && mProjectLoaded)
                {
                    if (mProgDialog.isShowing()) {
                        mProgDialog.dismiss();
                    }
                    Logger.Info("Project and signals loaded");
                    ProjectReadyUseCase projectReadyUseCase = new ProjectReadyUseCase();
                    RxBus.INSTANCE.send(projectReadyUseCase);
                }

            });
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
        }
    }


}
