package com.crestron.mobile.bcip;

import android.util.Log;

import com.crestron.cip.IplinkHandlerInterface;
import com.crestron.signal_transport.SignalListenerInterface;
import com.crestron.signal_transport.SignalTransportInterface;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>CIPHandler class </h1>
 * <p>
 * Used to handle connection to control system via x60 firmware
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CIPHandler implements IplinkHandlerInterface, SignalListenerInterface {
    private static final String TAG = CIPHandler.class.getName();
    private CIPCSConnectionHandler mCIPCSConnectionHandler;
    /**
     * List of Signals
     */
    private static final List<String> mSignalList = new ArrayList<String>();

    CIPHandler(CIPCSConnectionHandler cipCSConnectionHandler) {
        mCIPCSConnectionHandler = cipCSConnectionHandler;
    }

    @Override
    public void handleDigitalChanged(int subslotId, int joinId, boolean joinValue) {
        Log.v( "handleDigitalChanged: " , joinId + " / " + String.valueOf(joinValue));
        mCIPCSConnectionHandler.sendDigitalChanged(joinId, joinValue);
    }

    @Override
    public void handleAnalogChanged(int subslotId, int joinId, int joinValue) {
        Log.v( "handleAnalogChanged: " + joinId , " / " + String.valueOf(joinValue));
        mCIPCSConnectionHandler.sendAnalogChanged(joinId, joinValue);
    }

    @Override
    public void handleSerialChanged(int subslotId, int joinId, String joinValue) {
        Log.v( "handleAnalogChanged: " + joinId , " / " + String.valueOf(joinValue));
        mCIPCSConnectionHandler.sendSerialChanged(joinId, joinValue);
    }

    public void handleHardkeyEvent(int keyId, int keyValue) {
        Log.v( "handleHardkeyEvent: " , keyId + " / " + String.valueOf(keyValue));

    }

    @Override
    public void handleSmartObjectDigitalChanged(int subslotId, int joinId, boolean joinValue) {
        Log.v( "handleSmartObject" , joinId + " / " + String.valueOf(joinValue));
    }


    @Override
    public void handleSmartObjectAnalogChanged(int subslotId, int joinId, int joinValue) {
        Log.v( "handleSmartObjectA " , joinId + " / " + String.valueOf(joinValue));
    }


    @Override
    public void handleSmartObjectSerialChanged(int subslotId, int joinId, String joinValue) {
        Log.v( "handleSmartObjectSerial" , joinId + " / " + String.valueOf(joinValue));
    }


    @Override
    public void handleCresstoreSignalChange(String s, String s1) {
        Log.v( "handleCresstoreSignal: " , s + " / " + s1);
        mCIPCSConnectionHandler.sendObjectSignalChange(s, s);
    }

    @Override
    public void handleIntegerSignalChange(String s, int i) {
        Log.v( "handleIntegerSignal: " , s + " / " + String.valueOf(i));
    }

    @Override
    public void handleStringSignalChange(String s, String s1) {
        Log.v( "handleStringSignal: " , s + " / " + s1);
    }

    @Override
    public void handleBooleanSignalChange(String s, boolean b) {
        Log.v( "handleBooleanSignal: " , s + " / " + String.valueOf(b));
    }

    @Override
    public void handleHardkeySignalChange(String s, int i) {

    }

    @Override
    public void handleTransportCacheState(String var1, boolean var2) {

    }

    @Override
    public void handleDeviceInterfaceError(String var1, String var2) {

    }
}
