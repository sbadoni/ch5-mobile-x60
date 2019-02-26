package com.crestron.mobile.webui;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.crestron.mobile.MainActivity;
import com.crestron.mobile.R;
import com.crestron.mobile.bcip.CIPBooleanUseCase;
import com.crestron.mobile.bcip.CIPBooleanUseCaseResp;
import com.crestron.mobile.bcip.CIPConnectToCSUseCase;
import com.crestron.mobile.bcip.CIPIntegerUseCase;
import com.crestron.mobile.bcip.CIPIntegerUseCaseResp;
import com.crestron.mobile.bcip.CIPStringUseCase;
import com.crestron.mobile.bcip.CIPStringUseCaseResp;
import com.crestron.mobile.bcip.reservedjoin.Csig_Boolean_UseCase;
import com.crestron.mobile.bcip.reservedjoin.Csig_Integer_UseCase;
import com.crestron.mobile.bcip.reservedjoin.Csig_String_UseCase;
import com.crestron.mobile.bcip.reservedjoin.response.ReservedBooleanResponseUseCase;
import com.crestron.mobile.bcip.reservedjoin.response.ReservedIntegerResponseUseCase;
import com.crestron.mobile.bcip.reservedjoin.response.ReservedStringResponseUseCase;
import com.crestron.mobile.cssettings.CSPersistenceControlSystemEntryOpUseCase;
import com.crestron.mobile.cssettings.CSPersistenceCreateControlSystemEntryUseCase;
import com.crestron.mobile.cssettings.CSPersistenceDeleteControlSystemEntryUseCase;
import com.crestron.mobile.cssettings.CSPersistenceGetControlSystemsEntriesUseCase;
import com.crestron.mobile.cssettings.CSPersistenceUpdateControlSystemEntryUseCase;
import com.crestron.mobile.cssettings.model.ControlSystemEntry;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.projectmanagement.DownloadProjectUseCase;
import com.crestron.mobile.signal.SignalManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * <h1>UIEventHandler class </h1>
 * <p>
 * Handles events for the Web UI
 *
 * @author Colm Coady
 * @version 1.0
 */

public class UIEventHandler {

    private static final String TAG = UIEventHandler.class.getName();
    private Context mContext;
    private Gson gson = new Gson();
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private SignalManager mSignalManager;
    private InputStream mReservedJoinInputStream;


    private static final String START_PAGE = "index.html";
    private static final String USER_JSON = "user.json";
    private static final String SIGNAL_JOIN_MAP_JSON = "signalJoinMap.json";

    private class CaResult {
        String eventContext;
        boolean successFlag;
        int returnCode;
        Object returnObject;
    }


    UIEventHandler(Context context) {
        mContext = context;
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

        mSignalManager = SignalManager.getInstance();
        mSignalManager.init(null, mReservedJoinInputStream);
        initSetupUIUseCaseLiseners();
        initCIPUseCaseLiseners();
        initReservedUseCasesLiseners();
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

    /**
     * Initializes CIPUseCases (aka joins) listeners for RX Bux
     */
    private void initCIPUseCaseLiseners() {

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPIntegerUseCaseResp.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onIntegerUseCase));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPStringUseCaseResp.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onStringUseCase));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPBooleanUseCaseResp.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onBoolUseCase));
    }

    /**
     * Initializes ReservedUseCases listeners for RX Bux
     */

    private void initReservedUseCasesLiseners() {

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(ReservedBooleanResponseUseCase.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onReservedBoolUseCase));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(ReservedStringResponseUseCase.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onReservedStringUseCase));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(ReservedIntegerResponseUseCase.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onReservedIntUseCase));
    }

    /**
     * De-Initializes listeners for RX Bux
     */
    public void deinit() {
        mCompositeDisposable.dispose();
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
     * Sends a string signal(join) to RxBus
     *
     * @param signalName signal name
     * @param value      value
     */
    void sendStringUseCase(String signalName, String value) {
        try {
            CIPStringUseCase cipStrUseCase = new CIPStringUseCase(signalName);
            cipStrUseCase.setValue(value);
            RxBus.INSTANCE.send(cipStrUseCase);
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Sends a integer signal(join) to RxBus
     *
     * @param signalName signal name
     * @param value      value
     */
    void sendIntegerUseCase(String signalName, int value) {
        try {
            CIPIntegerUseCase cipIntegerUseCase = new CIPIntegerUseCase(signalName);
            cipIntegerUseCase.setValue(value);
            RxBus.INSTANCE.send(cipIntegerUseCase);
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Sends a boolean signal(join) to RxBus
     *
     * @param signalName signal name
     * @param value      value
     */
    void sendBoolUseCase(String signalName, boolean value) {
        try {
            CIPBooleanUseCase cipBoolUseCase = new CIPBooleanUseCase(signalName);
            cipBoolUseCase.setValue(value);
            RxBus.INSTANCE.send(cipBoolUseCase);
        } catch (Exception e) {
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

    /**
     * Sends a boolean signal(join) to RxBus
     *
     * @param signalName signal name
     * @param value      value
     */

    public void sendBooleanReservedUseCase(String signalName, boolean value) {
        try {
            Csig_Boolean_UseCase booleanReservedJoin = (Csig_Boolean_UseCase) mSignalManager.getReservedSignalUseCase(signalName);
            booleanReservedJoin.setValue(value);
            RxBus.INSTANCE.send(booleanReservedJoin);
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }

    }

    /**
     * Sends a string signal(join) to RxBus
     *
     * @param signalName signal name
     * @param value      value
     */

    public void sendStringReservedUseCase(String signalName, String value) {
        try {
            Csig_String_UseCase stringReservedJoin = (Csig_String_UseCase) mSignalManager.getReservedSignalUseCase(signalName);
            stringReservedJoin.setValue(value);
            RxBus.INSTANCE.send(stringReservedJoin);

        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Sends a integer signal(join) to RxBus
     *
     * @param signalName signal name
     * @param value      value
     */

    public void sendIntegerReservedUseCase(String signalName, int value) {
        try {
            Csig_Integer_UseCase integerReservedJoin = (Csig_Integer_UseCase) mSignalManager.getReservedSignalUseCase(signalName);
            integerReservedJoin.setValue(value);
            RxBus.INSTANCE.send(integerReservedJoin);
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Sends a serial join to user interface
     *
     * @param cipStringUseCaseResp CIP use case for serial
     */
    private void onStringUseCase(CIPStringUseCaseResp cipStringUseCaseResp) {
        try {
            updateUI("String", cipStringUseCaseResp.getUseCaseName(), "'" + cipStringUseCaseResp.getValue() + "'");
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Sends an analog join to user interface
     *
     * @param cipIntegerUseCaseResp CIP use case for analog
     */
    private void onIntegerUseCase(CIPIntegerUseCaseResp cipIntegerUseCaseResp) {
        try {
            updateUI("Integer", cipIntegerUseCaseResp.getUseCaseName(), Integer.toString(cipIntegerUseCaseResp.getValue()));
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Sends a digital join to user interface
     *
     * @param cipBooleanUseCaseResp CIP use case for digital
     */
    private void onBoolUseCase(CIPBooleanUseCaseResp cipBooleanUseCaseResp) {
        try {
            updateUI("Boolean", cipBooleanUseCaseResp.getUseCaseName(), Boolean.toString(cipBooleanUseCaseResp.getValue()));
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Sends an analog join to user interface
     *
     * @param reservedIntRespUseCase CIP use case for analog
     */
    private void onReservedIntUseCase(ReservedIntegerResponseUseCase reservedIntRespUseCase) {
        try {
            updateUI("Integer", reservedIntRespUseCase.getUseCaseName(), Integer.toString(reservedIntRespUseCase.getValue()));
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Sends a digital join to user interface
     *
     * @param reservedBoolRespUseCase Reserved use case for digital
     */
    private void onReservedBoolUseCase(ReservedBooleanResponseUseCase reservedBoolRespUseCase) {
        try {
            updateUI("Boolean", reservedBoolRespUseCase.getUseCaseName(), Boolean.toString(reservedBoolRespUseCase.getValue()));
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }

    }

    /**
     * Sends a digital join to user interface
     *
     * @param reservedStringRespUseCase Reserved use case for serial
     */

    private void onReservedStringUseCase(ReservedStringResponseUseCase reservedStringRespUseCase) {
        try {
            updateUI("String", reservedStringRespUseCase.getUseCaseName(), "'" + reservedStringRespUseCase.getValue() + "'");
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Executes javascript on webview
     *
     * @param type       join type: boolean, string, integer
     * @param signalName signal name
     * @param value      value
     */
    private void updateUI(String type, String signalName, String value) {
        try {
            MainActivity activity = (MainActivity) mContext;
            activity.sendToWebView("bridgeReceive" + type + "FromNative('" + signalName + "'," + value + ");");
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

}
