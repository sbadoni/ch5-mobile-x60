package com.crestron.blackbird.webui;

import android.content.Context;
import android.util.Log;

import com.crestron.logging.Logger;
import com.crestron.mobile.R;
import com.crestron.mobile.bcip.CIPBooleanUseCase;
import com.crestron.mobile.bcip.CIPBooleanUseCaseResp;
import com.crestron.mobile.bcip.CIPIntegerUseCase;
import com.crestron.mobile.bcip.CIPIntegerUseCaseResp;
import com.crestron.mobile.bcip.CIPObjectUseCase;
import com.crestron.mobile.bcip.CIPObjectUseCaseResp;
import com.crestron.mobile.bcip.CIPStringUseCase;
import com.crestron.mobile.bcip.CIPStringUseCaseResp;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.reservedjoin.Csig_Boolean_UseCase;
import com.crestron.mobile.reservedjoin.Csig_Boolean_UseCaseResp;
import com.crestron.mobile.reservedjoin.Csig_Integer_UseCase;
import com.crestron.mobile.reservedjoin.Csig_Integer_UseCaseResp;
import com.crestron.mobile.reservedjoin.Csig_String_UseCase;
import com.crestron.mobile.reservedjoin.Csig_String_UseCaseResp;
import com.crestron.mobile.signal.SignalManager;
import com.crestron.blackbird.MainActivity;
import com.google.gson.Gson;

import java.io.InputStream;

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
    private InputStream reservedJoinInpuStream;

    private static final String START_PAGE = "index.html";
    private static final String USER_JSON = "user.json";

    private class CaResult {
        String eventContext;
        boolean successFlag;
        int returnCode;
        Object returnObject;
    }


    UIEventHandler(Context context) {
        mContext = context;
        reservedJoinInpuStream= mContext.getResources().openRawResource(R.raw.reservedjoins);
    }

    /**
     * Initializes listeners for RX Bux
     */
    void init() {
        mSignalManager = SignalManager.getInstance();
        initCIPUseCaseLiseners();
        initReservedUseCaseListeners();
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

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPObjectUseCaseResp.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onObjectUseCase));
    }

    /**
     * Initializes reserved joins listeners for RX Bux
     */
    private void initReservedUseCaseListeners() {

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_Integer_UseCaseResp.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onIntReservedUseCase));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_Boolean_UseCaseResp.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onBoolReservedUseCase));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_String_UseCaseResp.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onStringReservedUseCase));
    }

    /**
     * De-Initializes listeners for RX Bux
     */
    public void deinit() {
        mCompositeDisposable.dispose();
    }



    /**
     * Sends a string signal(join) to RxBus
     *
     * @param signalName signal name
     * @param value      value
     */
    void sendStringUseCase(String signalName, String value) {
        try {
            Logger.Verbose( "sendStringUseCase: " + signalName+"/"+value);
            CIPStringUseCase cipStrUseCase = new CIPStringUseCase(signalName);
            cipStrUseCase.setValue(value);
            RxBus.INSTANCE.send(cipStrUseCase);
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
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
            Logger.Verbose( "sendIntegerUseCase: " + signalName+"/"+value);
            CIPIntegerUseCase cipIntegerUseCase = new CIPIntegerUseCase(signalName);
            cipIntegerUseCase.setValue(value);
            RxBus.INSTANCE.send(cipIntegerUseCase);
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
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
            Logger.Verbose( "sendBoolUseCase: " + signalName+"/"+value);
            CIPBooleanUseCase cipBoolUseCase = new CIPBooleanUseCase(signalName);
            cipBoolUseCase.setValue(value);
            RxBus.INSTANCE.send(cipBoolUseCase);
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
        }
    }

    /**
     * Determines each signal should be handled
     *
     * @param signalName        signal name
     * @param jsonEncodedObject JSON object
     */
    void sendObjectUseCase(String signalName, String jsonEncodedObject) {
        Logger.Verbose( "sendObjectUseCase: " + signalName+": "+jsonEncodedObject);
        CIPObjectUseCase cipObjectUseCase = new CIPObjectUseCase(signalName);
        cipObjectUseCase.setJsonString(jsonEncodedObject);
        RxBus.INSTANCE.send(cipObjectUseCase);
    }

    /**
     * Sends a int signal(reserved join) to RxBus
     *
     * @param signalName signal name
     * @param value      value
     */
    void sendIntReservedUseCase(String signalName, int value) {
        Logger.Verbose( "sendIntReservedUseCase: " + signalName+"/"+value);
        Csig_Integer_UseCase csig_integer_useCase = (Csig_Integer_UseCase) mSignalManager.getReservedSignalUseCase(signalName, true);
        csig_integer_useCase.setValue(value);
        RxBus.INSTANCE.send(csig_integer_useCase);
    }

    /**
     * Sends a bool signal(reserved join) to RxBus
     *
     * @param signalName signal name
     * @param value      value
     */
    void sendBoolReservedUseCase(String signalName, boolean value) {
        Logger.Verbose( "sendBoolReservedUseCase: " + signalName+"/"+value);
        Csig_Boolean_UseCase csig_boolean_useCase = (Csig_Boolean_UseCase) mSignalManager.getReservedSignalUseCase(signalName, true);
        csig_boolean_useCase.setValue(value);
        RxBus.INSTANCE.send(csig_boolean_useCase);
    }

    /**
     * Sends a string signal(reserved join) to RxBus
     *
     * @param signalName signal name
     * @param value      value
     */
    void sendStrReservedUseCase(String signalName, String value) {
        Logger.Verbose( "sendStrReservedUseCase: " + signalName+"/"+value);
        Csig_String_UseCase csig_string_useCase = (Csig_String_UseCase) mSignalManager.getReservedSignalUseCase(signalName, true);
        csig_string_useCase.setValue(value);
        RxBus.INSTANCE.send(csig_string_useCase);
    }

    /**
     * Sends a serial join to user interface
     *
     * @param cipStringUseCaseResp CIP use case for serial
     */
    private void onStringUseCase(CIPStringUseCaseResp cipStringUseCaseResp) {
        try {
            Logger.Verbose( "onStringUseCase: " + cipStringUseCaseResp.getUseCaseName()+"/"+cipStringUseCaseResp.getValue());
            updateUI("String", cipStringUseCaseResp.getUseCaseName(), "'"+cipStringUseCaseResp.getValue()+"'");
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
        }
    }

    /**
     * Sends an analog join to user interface
     *
     * @param cipIntegerUseCaseResp CIP use case for analog
     */
    private void onIntegerUseCase(CIPIntegerUseCaseResp cipIntegerUseCaseResp) {
        try {
            Logger.Verbose( "onIntegerUseCase: " + cipIntegerUseCaseResp.getUseCaseName()+"/"+cipIntegerUseCaseResp.getValue());
            updateUI("Integer", cipIntegerUseCaseResp.getUseCaseName(), Integer.toString(cipIntegerUseCaseResp.getValue()));
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
        }
    }

    /**
     * Sends a digital join to user interface
     *
     * @param cipBooleanUseCaseResp CIP use case for digital
     */
    private void onBoolUseCase(CIPBooleanUseCaseResp cipBooleanUseCaseResp) {
        try {
            Logger.Verbose( "onBoolUseCase: " + cipBooleanUseCaseResp.getUseCaseName()+"/"+cipBooleanUseCaseResp.getValue());
            updateUI("Boolean", cipBooleanUseCaseResp.getUseCaseName(), Boolean.toString(cipBooleanUseCaseResp.getValue()));
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
        }
    }

    /**
     * Sends an reserved join to user interface
     *
     * @param csig_integer_useCaseResp CIP use case for analog
     */
    private void onIntReservedUseCase(Csig_Integer_UseCaseResp csig_integer_useCaseResp) {
        try {
            Logger.Verbose( "onIntReservedUseCase: " + csig_integer_useCaseResp.getUseCaseName());
            updateUI("Integer", csig_integer_useCaseResp.getUseCaseName(), Integer.toString(csig_integer_useCaseResp.getValue()));
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
        }
    }

    /**
     * Sends a reserved join to user interface
     *
     * @param csig_boolean_useCaseResp CIP use case for analog
     */
    private void onBoolReservedUseCase(Csig_Boolean_UseCaseResp csig_boolean_useCaseResp) {
        try {
            Logger.Verbose( "onBoolReservedUseCase: " + csig_boolean_useCaseResp.getUseCaseName());
            updateUI("Boolean", csig_boolean_useCaseResp.getUseCaseName(), Boolean.toString(csig_boolean_useCaseResp.getValue()));
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
        }
    }

    /**
     * Sends a reserved join to user interface
     *
     * @param csig_string_useCaseResp CIP use case for analog
     */
    private void onStringReservedUseCase(Csig_String_UseCaseResp csig_string_useCaseResp) {
        try {
            Logger.Verbose( "onStringReservedUseCase: " + csig_string_useCaseResp.getUseCaseName());
            updateUI("String", csig_string_useCaseResp.getUseCaseName(), "'"+csig_string_useCaseResp.getValue()+"'");
        } catch (Exception e) {
            Logger.Error( "Error e: " + e.toString());
        }
    }


    private void onObjectUseCase(CIPObjectUseCaseResp cipObjectUseCaseResp){
        MainActivity activity = (MainActivity) mContext;
        activity.handleVideoCommand(cipObjectUseCaseResp.getJsonString());
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
            Logger.Error( "Error e: " + e.toString());
        }
    }

    /**
     * Checks if signal name is a 'fbNumber'
     *
     * @param signalName       signal name
     */
    private int IsFBNumber(String signalName)
    {
        int num = -1;
        if (signalName.startsWith("fb"))
        {
            String lastPart = signalName.substring(2);
            try {
                num = Integer.parseInt(lastPart);

            } catch (Exception e) { }
        }
        return num;
    }

    /**
     * Checks if a integer signal is not integer map
     *
     * @param signalName       signal name
     */
    public void subscribeIntegerSignal(String signalName) {
        int joinId = IsFBNumber(signalName);
        if (joinId > -1)
            mSignalManager.addIntegerSignal(signalName, joinId);
    }

    /**
     * Checks if a string signal is not string map
     *
     * @param signalName       signal name
     */
    public void subscribeStringSignal(String signalName) {
        int joinId = IsFBNumber(signalName);
        if (joinId > -1)
            mSignalManager.addStringSignal(signalName, joinId);
    }

    /**
     * Checks if a boolean signal is not boolean map
     *
     * @param signalName       signal name
     */
    public void subscribeBooleanSignal(String signalName) {
        int joinId = IsFBNumber(signalName);
        if (joinId > -1)
            mSignalManager.addBooleanSignal(signalName, joinId);

    }


}
