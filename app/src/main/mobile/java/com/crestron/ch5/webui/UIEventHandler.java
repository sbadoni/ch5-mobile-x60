package com.crestron.ch5.webui;

import android.content.Context;
import android.net.Uri;
import android.util.Log;


import com.crestron.ch5.MainActivity;
import com.crestron.ch5.R;
import com.crestron.mobile.bcip.CIPBooleanUseCaseResp;
import com.crestron.mobile.bcip.CIPConnectToCSUseCase;
import com.crestron.mobile.bcip.CIPIntegerUseCaseResp;
import com.crestron.mobile.bcip.CIPStringUseCaseResp;
import com.crestron.mobile.cssettings.CSPersistenceControlSystemEntryOpUseCase;
import com.crestron.mobile.cssettings.CSPersistenceCreateControlSystemEntryUseCase;
import com.crestron.mobile.cssettings.CSPersistenceDeleteControlSystemEntryUseCase;
import com.crestron.mobile.cssettings.CSPersistenceGetControlSystemsEntriesUseCase;
import com.crestron.mobile.cssettings.CSPersistenceUpdateControlSystemEntryUseCase;
import com.crestron.mobile.cssettings.model.ControlSystemEntry;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.projectmanagement.DownloadProjectUseCase;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

/**
 * <h1>UIEventHandler class </h1>
 * <p>
 * Handles events for the Web UI
 *
 * @author Colm Coady
 * @version 1.0
 */

public class UIEventHandler extends UIEvent{

   private static final String TAG = UIEventHandler.class.getName();

    UIEventHandler(Context context) {
        super(context);
        readJsonFileFromRawFolder();
    }

    /**
     * Read JSON File from RAW Folder
     */
    private void readJsonFileFromRawFolder() {
        mReservedJoinInputStream = mContext.getResources().openRawResource(R.raw.reserved);

    }

    /**
     * Initializes listeners for RX Bux
     */
    void init() {
        super.init();
        mSignalManager.init(null, mReservedJoinInputStream);
        initSetupUIUseCaseLiseners();

    }

    /**
     * Initializes Setup Web UI UseCases listeners for RX Bux
     */
    private void initSetupUIUseCaseLiseners() {

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CSPersistenceGetControlSystemsEntriesUseCase.Response.class)
                .subscribeOn(Schedulers.io())
                .subscribe(resp -> {
                    onGetControlSystemEntries(resp.getCaControlSystems(), resp.responseStatus);
                }));


        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CSPersistenceCreateControlSystemEntryUseCase.Response.class)
                .subscribeOn(Schedulers.io())
                .subscribe(resp -> {
                    onCreateControlSystemEntry(resp.getCaControlSystems(), resp.responseStatus);
                }));


        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CSPersistenceUpdateControlSystemEntryUseCase.Response.class)
                .subscribeOn(Schedulers.io())
                .subscribe(resp -> {
                    onUpdateControlSystemEntry(resp.responseStatus);
                }));


        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CSPersistenceDeleteControlSystemEntryUseCase.Response.class)
                .subscribeOn(Schedulers.io())
                .subscribe(resp -> {
                    onDeleteControlSystemEntry(resp.responseStatus);
                }));


        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPConnectToCSUseCase.Response.class)
                .subscribeOn(Schedulers.io())
                .subscribe(resp -> {
                    onConnectedToControlSystem(resp.getResponseStatus());
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPConnectToCSUseCase.Response.class)
                .subscribeOn(Schedulers.io())
                .subscribe(resp -> {
                    onConnectedToControlSystem(resp.getResponseStatus());
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(DownloadProjectUseCase.Response.class)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this::onDownloadProject));

    }

    void getControlSystemEntries() {
        RxBus.INSTANCE.send(new CSPersistenceGetControlSystemsEntriesUseCase().new Request());
    }

    /**
     * Dispatches a list of control systems to web UI
     *
     * @param caControlSystems List<ControlSystemEntry>
     * @param responseStatus   CSPersistenceControlSystemEntryOpUseCase.Status
     */
    private void onGetControlSystemEntries(List<ControlSystemEntry> caControlSystems, CSPersistenceControlSystemEntryOpUseCase.Status responseStatus) {

        try {
            MainActivity activity = (MainActivity) mContext;
            String strResultJson = "{}";

            CaResult caResult = new CaResult();
            caResult.eventContext = "getControlSystems";
            if (responseStatus == CSPersistenceControlSystemEntryOpUseCase.Status.SUCCESS) {
                caResult.successFlag = true;
                caResult.returnCode = 0;
                caResult.returnObject = caControlSystems;
                strResultJson = gson.toJson(caResult);
                updateUI("Object", "Csig.GetControlSystemsResp", strResultJson);
            } else if (responseStatus == CSPersistenceControlSystemEntryOpUseCase.Status.FAILED) {
                caResult.successFlag = false;
                caResult.returnCode = 0;
                caResult.returnObject = null;
                strResultJson = gson.toJson(caResult);
                updateUI("Object", "Csig.GetControlSystemsResp", strResultJson);
            }
        } catch (JsonSyntaxException e) {
            // TODO write exception(s) to logging module
        }
    }

    /**
     * Dispatches a list of control systems created to web UI
     *
     * @param caControlSystems List<ControlSystemEntry>
     * @param responseStatus   CSPersistenceControlSystemEntryOpUseCase.Status
     */
    private void onCreateControlSystemEntry(List<ControlSystemEntry> caControlSystems, CSPersistenceControlSystemEntryOpUseCase.Status responseStatus) {

        try {

            MainActivity activity = (MainActivity) mContext;
            String strResultJson = "{}";

            CaResult caResult = new CaResult();
            caResult.eventContext = "saveControlSystemEntry";
            if (responseStatus == CSPersistenceControlSystemEntryOpUseCase.Status.SUCCESS) {
                caResult.successFlag = true;
                caResult.returnCode = 0;
                caResult.returnObject = caControlSystems;
                strResultJson = gson.toJson(caResult);
                updateUI("Object", "Csig.SaveControlSystemEntryResp", strResultJson);
            } else if (responseStatus == CSPersistenceControlSystemEntryOpUseCase.Status.FAILED) {
                caResult.successFlag = false;
                caResult.returnCode = 0;
                caResult.returnObject = null;
                strResultJson = gson.toJson(caResult);
                updateUI("Object", "Csig.SaveControlSystemEntryResp", strResultJson);
            }
        } catch (JsonSyntaxException e) {
            // TODO write exception(s) to logging module
        }
    }

    /**
     * Dispatches a list of deleted control systems to web UI
     *
     * @param responseStatus CSPersistenceControlSystemEntryOpUseCase.Status
     */
    private void onDeleteControlSystemEntry(CSPersistenceControlSystemEntryOpUseCase.Status responseStatus) {

        try {

            MainActivity activity = (MainActivity) mContext;
            String strResultJson = "{}";
            CaResult caResult = new CaResult();
            caResult.eventContext = "deleteControlSystemEntry";

            if (responseStatus == CSPersistenceControlSystemEntryOpUseCase.Status.SUCCESS) {
                caResult.successFlag = true;
                caResult.returnCode = 0;
                caResult.returnObject = new Object();
                strResultJson = gson.toJson(caResult);
                updateUI("Object", "Csig.DeleteControlSystemEntryResp", strResultJson);

            } else if (responseStatus == CSPersistenceControlSystemEntryOpUseCase.Status.FAILED) {
                caResult.successFlag = false;
                caResult.returnCode = 0;
                caResult.returnObject = new Object();
                strResultJson = gson.toJson(caResult);
                updateUI("Object", "Csig.DeleteControlSystemEntryResp", strResultJson);
            }
        } catch (JsonSyntaxException e) {
            // TODO write exception(s) to logging module
        }
    }

    /**
     * Dispatches a list of updated control systems to web UI
     *
     * @param responseStatus CSPersistenceControlSystemEntryOpUseCase.Status
     */
    private void onUpdateControlSystemEntry(CSPersistenceControlSystemEntryOpUseCase.Status responseStatus) {

        try {
            ControlSystemEntry caControlSystems = new ControlSystemEntry();
            MainActivity activity = (MainActivity) mContext;

            String strResultJson = "{}";
            CaResult caResult = new CaResult();
            caResult.eventContext = "saveControlSystemEntry";

            if (responseStatus == CSPersistenceControlSystemEntryOpUseCase.Status.SUCCESS) {
                caResult.successFlag = true;
                caResult.returnCode = 0;
                caResult.returnObject = caControlSystems;
                strResultJson = gson.toJson(caResult);
                updateUI("Object", "Csig.SaveControlSystemEntryResp", strResultJson);
            } else if (responseStatus == CSPersistenceControlSystemEntryOpUseCase.Status.FAILED) {
                caResult.successFlag = false;
                caResult.returnCode = 0;
                caResult.returnObject = null;
                strResultJson = gson.toJson(caResult);
                updateUI("Object", "Csig.SaveControlSystemEntryResp", strResultJson);
            }
        } catch (JsonSyntaxException e) {
            // TODO write exception(s) to logging module
        }
    }

    /**
     * Requests a connection control system
     *
     * @param jsonStr control system connection information
     */
    void connectToControlSystem(String jsonStr) {
        ControlSystemEntry controlSystemEntry;
        try {
            MainActivity activity = (MainActivity) mContext;
            controlSystemEntry = gson.fromJson(jsonStr, ControlSystemEntry.class);

            CIPConnectToCSUseCase.Request connectReq = new CIPConnectToCSUseCase().new Request(controlSystemEntry);

            activity.showProgressMsg("Connecting...");

            RxBus.INSTANCE.send(connectReq);

        } catch (JsonSyntaxException e) {
            // TODO write exception(s) to logging module
        }
    }

    /**
     * Sends a connection status response from a control system connection
     *
     * @param responseStatus CIPConnectToCSUseCase.Status
     */
    private void onConnectedToControlSystem(CIPConnectToCSUseCase.Status responseStatus) {

        try {

            MainActivity activity = (MainActivity) mContext;
            String strResultJson = "{}";
            CaResult caResult = new CaResult();
            caResult.eventContext = "connectToControlSystem";

            if (responseStatus == CIPConnectToCSUseCase.Status.CONNECTED) {
                caResult.successFlag = true;
                caResult.returnCode = 0;
                caResult.returnObject = new Object();
                strResultJson = gson.toJson(caResult);
                updateUI("Object", "Csig.ConnectToControlSystemResp", strResultJson);
            } else if (responseStatus == CIPConnectToCSUseCase.Status.NOTCONNECTED) {
                caResult.successFlag = false;
                caResult.returnCode = 0;
                caResult.returnObject = new Object();
                strResultJson = gson.toJson(caResult);
                updateUI("Object", "Csig.ConnectToControlSystemResp", strResultJson);
            }
        } catch (JsonSyntaxException e) {
            // TODO write exception(s) to logging module
        }
    }

    /**
     * Requests a control system information to be saved
     *
     * @param jsonStr control system connection information
     */
    void saveControlSystemEntry(String jsonStr) {

        ControlSystemEntry controlSystemEntry;
        try {

            controlSystemEntry = gson.fromJson(jsonStr, ControlSystemEntry.class);
            int csid = Integer.parseInt(controlSystemEntry.getControlSystemID());

            if (csid <= 0) {
                controlSystemEntry.setControlSystemID("");
                List<ControlSystemEntry> caControlSystems = new ArrayList<>();

                caControlSystems.add(controlSystemEntry);
                CSPersistenceCreateControlSystemEntryUseCase.Request createRequest = new CSPersistenceCreateControlSystemEntryUseCase().new Request(caControlSystems);
                RxBus.INSTANCE.send(createRequest);
            } else {
                List<ControlSystemEntry> caControlSystems = new ArrayList<>();

                caControlSystems.add(controlSystemEntry);
                CSPersistenceUpdateControlSystemEntryUseCase.Request updateRequest = new CSPersistenceUpdateControlSystemEntryUseCase().new Request(caControlSystems);
                RxBus.INSTANCE.send(updateRequest);
            }
        } catch (JsonSyntaxException e) {
            // TODO write exception(s) to logging module
        }
    }

    /**
     * Requests a control system information to be deleted
     *
     * @param jsonStr control system connection information
     */
    void deleteControlSystemEntry(String jsonStr) {
        ControlSystemEntry controlSystemEntry;
        try {
            controlSystemEntry = gson.fromJson(jsonStr, ControlSystemEntry.class);

            List<ControlSystemEntry> caControlSystems = new ArrayList<>();

            caControlSystems.add(controlSystemEntry);

            CSPersistenceDeleteControlSystemEntryUseCase.Request deleteRequest = new CSPersistenceDeleteControlSystemEntryUseCase().new Request(caControlSystems);
            RxBus.INSTANCE.send(deleteRequest);

        } catch (JsonSyntaxException e) {
            // TODO write exception(s) to logging module
        }
    }

    /**
     * Handles loading of project from control system
     *
     * @param downloadProjectUseCaseResp DownloadProjectUseCase response
     */
    private void onDownloadProject(DownloadProjectUseCase.Response downloadProjectUseCaseResp) {

        try {
            MainActivity activity = (MainActivity) mContext;
            if (downloadProjectUseCaseResp.responseStatus == DownloadProjectUseCase.Status.SUCCESS) {
                String projectMainPage = downloadProjectUseCaseResp.projectPath + "/" + START_PAGE;
                //String userJSONFilePath = downloadProjectUseCaseResp.projectPath + "/" +  USER_JSON;
                String signalJoinMapJSONFilePath = downloadProjectUseCaseResp.projectPath + "/config/" + SIGNAL_JOIN_MAP_JSON;

                //File userJSONFile = new File(userJSONFilePath);
                File userJSONFile = new File(signalJoinMapJSONFilePath);
                if (userJSONFile.exists()) {
                    mSignalManager.init(userJSONFile, null);
                }
                File indexFile = new File(projectMainPage);
                if (indexFile.exists()) {
                    Uri projectMainPageUri = Uri.fromFile(indexFile);
                    activity.loadDownloadedProject(projectMainPageUri.toString());
                } else {
                    Log.d(TAG, "File does not exists");
                }
                activity.dismissProgressMsg();

            } else if (downloadProjectUseCaseResp.responseStatus == DownloadProjectUseCase.Status.FAILED) {
                activity.dismissProgressMsg();
                String strResultJson = "{}";
                CaResult caResult = new CaResult();
                caResult.eventContext = "downloadProject";
                caResult.successFlag = false;
                caResult.returnCode = 0;
                caResult.returnObject = new Object();
                strResultJson = gson.toJson(caResult);
                updateUI("Object", "Csig.downloadProjectResp", strResultJson);
            }
        } catch (Exception e) {
            // TODO write exception(s) to logging module
            Log.d(TAG, "Error e: " + e.toString());
        }
    }
    /**
     * Determines each signal should be handled
     *
     * @param signalName        signal name
     * @param jsonEncodedObject JSON object
     */
    void sendObjectUseCase(String signalName, String jsonEncodedObject) {
        try {
            switch (signalName) {
                case "Csig.GetControlSystems":
                    getControlSystemEntries();
                    break;
                case "Csig.SaveControlSystemEntry":
                    saveControlSystemEntry(jsonEncodedObject);
                    break;
                case "Csig.DeleteControlSystemEntry":
                    deleteControlSystemEntry(jsonEncodedObject);
                    break;
                case "Csig.ConnectToControlSystem":
                    connectToControlSystem(jsonEncodedObject);
                    break;
            }

        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }
}
