package com.crestron.ch5.webui;

import android.content.Context;
import android.util.Log;

import com.crestron.ch5.MainActivity;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.bcip.CIPBooleanUseCase;
import com.crestron.mobile.bcip.CIPBooleanUseCaseResp;
import com.crestron.mobile.bcip.CIPIntegerUseCase;
import com.crestron.mobile.bcip.CIPIntegerUseCaseResp;
import com.crestron.mobile.bcip.CIPStringUseCase;
import com.crestron.mobile.bcip.CIPStringUseCaseResp;
import com.crestron.mobile.bcip.reservedjoin.Csig_Boolean_UseCase;
import com.crestron.mobile.bcip.reservedjoin.Csig_Boolean_UseCaseResp;
import com.crestron.mobile.bcip.reservedjoin.Csig_Integer_UseCase;
import com.crestron.mobile.bcip.reservedjoin.Csig_Integer_UseCaseResp;
import com.crestron.mobile.bcip.reservedjoin.Csig_String_UseCase;
import com.crestron.mobile.bcip.reservedjoin.Csig_String_UseCaseResp;

import com.crestron.mobile.signal.SignalManager;
import com.google.gson.Gson;

import java.io.InputStream;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

abstract public class UIEvent {

    protected static final String TAG = UIEvent.class.getName();
    protected Context mContext;
    protected Gson gson = new Gson();
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    protected SignalManager mSignalManager;
    protected InputStream mReservedJoinInputStream;

    protected static final String START_PAGE = "index.html";
    protected static final String USER_JSON = "user.json";


    public UIEvent( Context mContext){
       this.mContext = mContext;
    }

    protected class CaResult {
        String eventContext;
        boolean successFlag;
        int returnCode;
        Object returnObject;
    }

    void init(){
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

    }
    /**
     * Initializes ReservedUseCases listeners for RX Bux
     */

    private void initReservedUseCaseListeners() {

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_Boolean_UseCaseResp.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onReservedBoolUseCase));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_String_UseCaseResp.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onReservedStringUseCase));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_Integer_UseCaseResp.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onReservedIntUseCase));
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
     * Sends a boolean signal(join) to RxBus
     *
     * @param signalName signal name
     * @param value      value
     */

    public void sendBooleanReservedUseCase(String signalName, boolean value) {
        try {
            Csig_Boolean_UseCase booleanReservedJoin = (Csig_Boolean_UseCase) mSignalManager.getReservedSignalUseCase(signalName,true);
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
            Csig_String_UseCase stringReservedJoin = (Csig_String_UseCase) mSignalManager.getReservedSignalUseCase(signalName,true);
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
            Csig_Integer_UseCase integerReservedJoin = (Csig_Integer_UseCase) mSignalManager.getReservedSignalUseCase(signalName,true);
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
    protected void onStringUseCase(CIPStringUseCaseResp cipStringUseCaseResp) {
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
    protected void onIntegerUseCase(CIPIntegerUseCaseResp cipIntegerUseCaseResp) {
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
    protected void onBoolUseCase(CIPBooleanUseCaseResp cipBooleanUseCaseResp) {
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
    protected void onReservedIntUseCase(Csig_Integer_UseCaseResp reservedIntRespUseCase) {
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
    protected void onReservedBoolUseCase(Csig_Boolean_UseCaseResp reservedBoolRespUseCase) {
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

    protected void onReservedStringUseCase(Csig_String_UseCaseResp reservedStringRespUseCase) {
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
    protected void updateUI(String type, String signalName, String value) {
        try {
            MainActivity activity = (MainActivity) mContext;
            activity.sendToWebView("bridgeReceive" + type + "FromNative('" + signalName + "'," + value + ");");
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }
}
