package com.crestron.ch5.webui;

import android.content.Context;


import com.crestron.ch5.MainActivity;
import com.crestron.ch5.R;
import com.crestron.logging.Logger;
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

public class UIEventHandler extends UIEvent {

    private static final String TAG = UIEventHandler.class.getName();
    UIEventHandler(Context context) {
        super(context);
        //mContext = context;
        mReservedJoinInputStream= mContext.getResources().openRawResource(R.raw.reservedjoins);
    }

    /**
     * Initializes listeners for RX Bux
     */
    void init() {
        mCompositeDisposable.add(RxBus.INSTANCE
            .listen(CIPObjectUseCaseResp.class)
            .subscribeOn(Schedulers.io())
            .subscribe(this::onObjectUseCase));
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

    private void onObjectUseCase(CIPObjectUseCaseResp cipObjectUseCaseResp){
        MainActivity activity = (MainActivity) mContext;
        activity.handleVideoCommand(cipObjectUseCaseResp.getJsonString());
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
