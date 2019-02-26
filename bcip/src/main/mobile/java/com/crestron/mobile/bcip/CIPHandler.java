package com.crestron.mobile.bcip;

import android.util.Log;

import com.crestron.cip.*;
/**
 * <h1>CIPHandler class </h1>
 * <p>
 * Used to handle connection to control system
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CIPHandler implements BlackbirdHandlerInterface {
    private static final String TAG = CIPHandler.class.getName();
    private CIPCSConnectionHandler  mCIPCSConnectionHandler;
    CIPHandler(CIPCSConnectionHandler cipCSConnectionHandler)
    {
        mCIPCSConnectionHandler = cipCSConnectionHandler;
    }

    @Override
    public void handleDigitalChanged(int subslotId, int joinId, boolean joinValue){
        Log.d(TAG, "handleDigitalChanged: " + String.valueOf(joinId) + " / " + String.valueOf(joinValue));
        mCIPCSConnectionHandler.sendDigitalChanged(joinId, joinValue);
    }

    @Override
    public void handleReservedDigitalChanged(int subslotId, int joinId, boolean joinValue) {
        Log.d(TAG, "handleReservedDigitalChanged: " + String.valueOf(joinId) + " / " + String.valueOf(joinValue));
        mCIPCSConnectionHandler.sendReservedDigitalChanged(joinId, joinValue);

    }

    @Override
    public void handleAnalogChanged(int subslotId, int joinId, int joinValue) {
        Log.d(TAG, "handleAnalogChanged: " + String.valueOf(joinId) + " / " + String.valueOf(joinValue));
        mCIPCSConnectionHandler.sendAnalogChanged(joinId, joinValue);
    }

    @Override
    public void handleReservedAnalogChanged(int subslotId, int joinId, int joinValue) {
        Log.d(TAG, "handleReservedAnalogChanged: " + String.valueOf(joinId) + " / " + String.valueOf(joinValue));
        mCIPCSConnectionHandler.sendReservedAnalogChanged(joinId, joinValue);
    }

    @Override
    public void handleSerialChanged(int subslotId, int joinId, String joinValue) {
        Log.d(TAG, "handleSerialChanged: " + String.valueOf(joinId) + " / " + joinValue);
        mCIPCSConnectionHandler.sendSerialChanged(joinId, joinValue);
    }

    @Override
    public void handleReservedSerialChanged(int subslotId, int joinId, String joinValue) {
        Log.d(TAG, "handleReservedSerialChanged: " + String.valueOf(joinId) + " / " + joinValue);
        mCIPCSConnectionHandler.sendReservedSerialChanged(joinId, joinValue);

    }

    @Override
    public void handleUIState(int oldState, int newState)
    {
        Log.d(TAG, "handleUIState: " + String.valueOf(oldState) + " / " + String.valueOf(newState));
        mCIPCSConnectionHandler.updateUIState(oldState, newState);
    }

    @Override
    public DigitalJoin handleDigitalQuery(int subslotId, int joinId) {
        DigitalJoin j = new DigitalJoin();
        j.subSlot =0;
        j.value =true;
        j.joinNumber =0;
        Log.d(TAG, "DigitalJoin: ");
        return j;
    }

    /**
     * The control system is querying a reserved join that we're responsible for.
     *
     * @return An AnalogJoin object or null if the value cannot be provided.
     */
    @Override
    public AnalogJoin handleAnalogQuery(int subslotId, int joinId) {
        AnalogJoin j = new AnalogJoin();
        j.subSlot =0;
        j.value = 0;
        j.joinNumber =0;
        Log.d(TAG, "AnalogJoin: ");
        return j;
    }

    /**
     * The control system is querying a reserved join that we're responsible for.
     *
     * @return A SerialJoin object or null if the value cannot be provided.
     */
    @Override
    public SerialJoin handleSerialQuery(int subslotId, int joinId) {
        SerialJoin j = new SerialJoin();
        j.subSlot =0;
        j.value = "test";
        j.joinNumber =0;
        Log.d(TAG, "SerialJoin: ");
        return j;
    }

    @Override
    public void handleProjectNameWithCredentials(BcipConnectionInfo bcipConnectionInfo, String projectName) {
        Log.d(TAG, "handleProjectNameWithCredentials: " + bcipConnectionInfo.toString());
        Log.d(TAG, "handleProjectNameWithCredentials: " + projectName);
        mCIPCSConnectionHandler.downloadProject(bcipConnectionInfo, projectName);
    }

    @Override
    public void handleProjectPath(String projectPath) {
        Log.d(TAG, "handleProjectName: ");
    }

}
