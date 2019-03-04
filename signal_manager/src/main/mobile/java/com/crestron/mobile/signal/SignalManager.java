package com.crestron.mobile.signal;


import android.util.Log;

import com.crestron.mobile.bcip.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>SignalManager class </h1>
 * <p>
 * Handles conversion of signals to use cases and joins to signals
 *
 * @author Colm Coady
 * @version 1.0
 */

public class SignalManager {

    private static final String TAG = SignalManager.class.getSimpleName();
    public static final String RESERVED_JOIN_PACKAGE_NAME = "com.crestron.mobile.bcip.reservedjoin.request.Csig_";
    public static final String USE_CASE = "_UseCase";

    private static volatile SignalManager sSingleInstance;

    /**
     * Thread safe join id, integer SignalInfo map
     */
    private ConcurrentHashMap<Integer, SignalInfo> mIntJoinSignalMap = new ConcurrentHashMap<>();

    /**
     * Thread safe join id, string SignalInfo map
     */
    private ConcurrentHashMap<Integer, SignalInfo> mStrJoinSignalMap = new ConcurrentHashMap<>();

    /**
     * Thread safe join id, boolean SignalInfo map
     */
    private ConcurrentHashMap<Integer, SignalInfo> mBoolJoinSignalMap = new ConcurrentHashMap<>();

    /**
     * Thread safe signal name, string use case map
     */
    private ConcurrentHashMap<String, CIPStringUseCase> mStrSignalUseCaseMap = new ConcurrentHashMap<>();

    /**
     * Thread safe signal name, integer use case map
     */
    private ConcurrentHashMap<String, CIPIntegerUseCase> mIntSignalUseCaseMap = new ConcurrentHashMap<>();

    /**
     * Thread safe signal name, boolean use case map
     */
    private ConcurrentHashMap<String, CIPBooleanUseCase> mBoolSignalUseCaseMap = new ConcurrentHashMap<>();

    /**
     * Thread safe signal name, integer Reserved Join map
     */
    private ConcurrentHashMap<Integer,SignalInfo> mIntegerReservedJoinMap = new ConcurrentHashMap<>();

    /**
     * Thread safe signal name, boolean Reserved Join map
     */
    private ConcurrentHashMap<Integer,SignalInfo> mBooleanReservedJoinMap = new ConcurrentHashMap<>();

    /**
     * Thread safe signal name, String Reserved Join map
     */
    private ConcurrentHashMap<Integer,SignalInfo> mStringReservedJoinMap = new ConcurrentHashMap<>();



    private SignalManager() {

        if (sSingleInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SignalManager getInstance() {

        if (sSingleInstance == null) {
            synchronized (SignalManager.class) {

                if (sSingleInstance == null) sSingleInstance = new SignalManager();
            }
        }
        return sSingleInstance;
    }

    /**
     * Loads user signals into respective maps
     *
     * @param userSignalJSONFile user signal JSON file path
     * @param reservedSignalJSONFile reserved signal JSON file path
     */
    public void init(File userSignalJSONFile, InputStream reservedSignalJSONFile )
    {

        try {

            clean();
            SignalLoader signalLoader = new SignalLoader();

            if(userSignalJSONFile != null)
            signalLoader.loadUserSignals(userSignalJSONFile);

            if(reservedSignalJSONFile != null)
                parseReservedJoin(signalLoader,reservedSignalJSONFile);

            if (signalLoader.getUserSignals() != null) {
                // Add user joins coming from UI
                for (int i = 0; i < signalLoader.getUserSignals().string.outbound.size(); i++) {
                    SignalInfo signalInfo = signalLoader.getUserSignals().string.outbound.get(i);
                    CIPStringUseCase cipStringUseCase = new CIPStringUseCase(signalInfo.getSignalName(), signalInfo.getJoinId(), signalInfo.getControlJoinId());
                    mStrSignalUseCaseMap.put(signalInfo.getSignalName(), cipStringUseCase);
                }

                for (int i = 0; i < signalLoader.getUserSignals().booleanT.outbound.size(); i++) {
                    SignalInfo signalInfo = signalLoader.getUserSignals().booleanT.outbound.get(i);
                    CIPBooleanUseCase cipBooleanUseCase = new CIPBooleanUseCase(signalInfo.getSignalName(), signalInfo.getJoinId(), signalInfo.getControlJoinId());
                    mBoolSignalUseCaseMap.put(signalInfo.getSignalName(), cipBooleanUseCase);
                }

                for (int i = 0; i < signalLoader.getUserSignals().numeric.outbound.size(); i++) {
                    SignalInfo signalInfo = signalLoader.getUserSignals().numeric.outbound.get(i);
                    CIPIntegerUseCase cipIntegerUseCase = new CIPIntegerUseCase(signalInfo.getSignalName(), signalInfo.getJoinId(), signalInfo.getControlJoinId());
                    mIntSignalUseCaseMap.put(signalInfo.getSignalName(), cipIntegerUseCase);
                }

                // Add user joins coming from control system
                for (int i = 0; i < signalLoader.getUserSignals().string.inbound.size(); i++) {
                    mStrJoinSignalMap.put((int) signalLoader.getUserSignals().string.inbound.get(i).getJoinId(), signalLoader.getUserSignals().string.inbound.get(i));
                }

                for (int i = 0; i < signalLoader.getUserSignals().booleanT.inbound.size(); i++) {
                    mBoolJoinSignalMap.put((int) signalLoader.getUserSignals().booleanT.inbound.get(i).getJoinId(), signalLoader.getUserSignals().booleanT.inbound.get(i));
                }

                for (int i = 0; i < signalLoader.getUserSignals().numeric.inbound.size(); i++) {
                    mIntJoinSignalMap.put((int) signalLoader.getUserSignals().numeric.inbound.get(i).getJoinId(), signalLoader.getUserSignals().numeric.inbound.get(i));
                }
            }
        }
        catch (Exception e)

        {
            // TODO log exception to logging module
           Log.d(TAG,e.getMessage()) ;
        }

    }

    private void parseReservedJoin(SignalLoader signalLoader, InputStream reservedSignalJSONFile) throws IOException {
        signalLoader.loadReservedSignals(reservedSignalJSONFile);
        ReservedSignals reservedSignals = signalLoader.getReservedSignals();
        if(reservedSignals != null){

            /**
             * Retrieves join id and  signal name  from fromUI
             */
            if((reservedSignals.getSignals().getFromUI().getBooleanTMap()!=null) &&
                    (reservedSignals.getSignals().getFromUI().getBooleanTMap().size()>0)) {
                for (String fromUIBooleanSignal : reservedSignals.getSignals().getFromUI().getBooleanTMap().keySet()) {
                    SignalInfo fromUIBooleanSignalInfo = reservedSignals.getSignals().getFromUI().getBooleanTMap().get(fromUIBooleanSignal);
                    mBooleanReservedJoinMap.put((int) fromUIBooleanSignalInfo.getJoinId(), fromUIBooleanSignalInfo);

                }
            }

            if((reservedSignals.getSignals().getFromUI().getString() !=null)&&
                    (reservedSignals.getSignals().getFromUI().getString().size()>0)) {
                for (String fromUIStringSignal : reservedSignals.getSignals().getFromUI().getString().keySet()) {
                    SignalInfo fromUIStringSignalInfo = reservedSignals.getSignals().getFromUI().getString().get(fromUIStringSignal);
                    mStringReservedJoinMap.put((int) fromUIStringSignalInfo.getJoinId(), fromUIStringSignalInfo);

                }
            }

            if((reservedSignals.getSignals().getFromUI().getNumeric() != null) &&
                    (reservedSignals.getSignals().getFromUI().getNumeric().size()>0)) {
                for (String fromUINumericSignal : reservedSignals.getSignals().getFromUI().getNumeric().keySet()) {
                    SignalInfo fromUINumericSignalInfo = reservedSignals.getSignals().getFromUI().getNumeric().get(fromUINumericSignal);
                    mIntegerReservedJoinMap.put((int) fromUINumericSignalInfo.getJoinId(), fromUINumericSignalInfo);

                }
            }

            /**
             * Retrieves join id and  signal name  from toUI
             */

            if((reservedSignals.getSignals().getToUI().getBooleanTMap() !=null) &&
                    (reservedSignals.getSignals().getToUI().getBooleanTMap().size()>0)) {
                for (String toUIBooleanSignal : reservedSignals.getSignals().getToUI().getBooleanTMap().keySet()) {
                    SignalInfo toUIBooleanSignalInfo = reservedSignals.getSignals().getToUI().getBooleanTMap().get(toUIBooleanSignal);
                    mBooleanReservedJoinMap.put((int) toUIBooleanSignalInfo.getJoinId(), toUIBooleanSignalInfo);

                }
            }

            if((reservedSignals.getSignals().getToUI().getString() != null)
                    && (reservedSignals.getSignals().getToUI().getString().size()>0)) {
                for (String toUIStringSignal : reservedSignals.getSignals().getToUI().getString().keySet()) {
                    SignalInfo toUIStringSignalInfo = reservedSignals.getSignals().getToUI().getString().get(toUIStringSignal);
                    mStringReservedJoinMap.put((int) toUIStringSignalInfo.getJoinId(), toUIStringSignalInfo);

                }
            }

            if((reservedSignals.getSignals().getToUI().getNumeric() != null)
                    && (reservedSignals.getSignals().getToUI().getNumeric().size()>0)) {
                for (String toUINumericSignal : reservedSignals.getSignals().getToUI().getNumeric().keySet()) {
                    SignalInfo toUINumericSignalInfo = reservedSignals.getSignals().getToUI().getNumeric().get(toUINumericSignal);
                    mIntegerReservedJoinMap.put((int) toUINumericSignalInfo.getJoinId(), toUINumericSignalInfo);

                }
            }
            Log.d(TAG,"Boolean"+mBooleanReservedJoinMap.toString());
            Log.d(TAG,"String"+mStringReservedJoinMap.toString());



        }
    }


    public Object getReservedSignalUseCase(String reservedEvent){

        Object  reservedSignalObject = null;
        String qualifiedSignalName = null;


        try{
            qualifiedSignalName = RESERVED_JOIN_PACKAGE_NAME + reservedEvent + USE_CASE;

            Class<?> mClass = Class.forName(qualifiedSignalName);
            reservedSignalObject = mClass.newInstance();

        }catch (InstantiationException e){
            Log.d(TAG, toString());
        }
        catch (ClassNotFoundException e){
            Log.d(TAG, e.toString());
        }
        catch (IllegalAccessException e){
            Log.d(TAG, e.toString());
        }
        catch (Exception e){
            Log.d(TAG, e.toString());
        }

        return reservedSignalObject;
    }



    /**
     * Get Reserved Signal Use Case from Signal Name
     *
     * @param signalName
     * @param fromUI
     * @return
     */

    //SAMEER NEED TO CHECK PACKAGENAME
    public Object getReservedSignalUseCase(String signalName, boolean fromUI) {
        Object reservedSignalObject = null;
        String qualifiedSignalName = null;
        signalName = handleSpecialCharSignals(signalName);
        try {
            if (fromUI) {
                qualifiedSignalName = "com.crestron.mobile.reservedjoin.request.Csig_" + signalName + "_UseCase";
            } else {
                qualifiedSignalName = "com.crestron.mobile.reservedjoin.response.Csig_" + signalName + "_UseCaseResp";
            }
            Class<?> aClass = Class.forName(qualifiedSignalName);
            reservedSignalObject = aClass.newInstance();
        } catch (InstantiationException e) {
            Log.e("Error e: " , e.toString());;
        } catch (ClassNotFoundException e) {
            Log.e( "Error e: " , e.toString());;
        } catch (IllegalAccessException e) {
            Log.e( "Error e: " , e.toString());;
        } catch (Exception e) {
            Log.e( "Error e: " , e.toString());;
        }
        return reservedSignalObject;
    }


    /**
     * Handling  special char signal Name
     *
     * @param signalName
     * @return
     */
    public String handleSpecialCharSignals(String signalName) {
        if (signalName.equals("Dial_*")) {
            signalName = "Dial_Star";
        } else if (signalName.equals("Dial_/#")) {
            signalName = "Dial_SlashHash";
        }
        return signalName;
    }

    private void clean() {

        mStrSignalUseCaseMap.clear();
        mBoolSignalUseCaseMap.clear();
        mIntSignalUseCaseMap.clear();
        mStrJoinSignalMap.clear();
        //mIntegerReservedJoinMap.clear();
        //mBooleanReservedJoinMap.clear();
       // mStringReservedJoinMap.clear();
    }



    /**
     * Retrieves an analog join id using signal name
     *
     * @param signalName signal name
     * @return join id
     */
    public int getIntegerUseCaseId(String signalName) {
        CIPIntegerUseCase cipIntegerUseCase = mIntSignalUseCaseMap.get(signalName);
        if (cipIntegerUseCase != null)
            return cipIntegerUseCase.getUseCaseId();
        else
            return -1;
    }

    /**
     * Retrieves a serial join id using signal name
     *
     * @param signalName signal name
     * @return join id
     */
    public int getStringUseCaseId(String signalName) {
        CIPStringUseCase cipStringUseCase = mStrSignalUseCaseMap.get(signalName);
        if (cipStringUseCase != null)
            return cipStringUseCase.getUseCaseId();
        else
            return -1;
    }

    /**
     * Retrieves a digital join id using signal name
     *
     * @param signalName signal name
     * @return join id
     */
    public int getBoolUseCaseId(String signalName) {
        CIPBooleanUseCase cipBooleanUseCase = mBoolSignalUseCaseMap.get(signalName);
        if (cipBooleanUseCase != null)
            return cipBooleanUseCase.getUseCaseId();
        else
            return -1;
    }

    /**
     * Retrieves SignalInfo object using an analog join id
     *
     * @param joinId join Id
     * @return SignalInfo
     */
    public SignalInfo getIntegerSignalInfo(int joinId) {
        return mIntJoinSignalMap.get(joinId);
    }

    /**
     * Retrieves SignalInfo object using a serial join id
     *
     * @param joinId join Id
     * @return SignalInfo
     */
    public SignalInfo getStringSignalInfo(int joinId) {
        return mStrJoinSignalMap.get(joinId);
    }

    /**
     * Retrieves SignalInfo object using a boolean join id
     *
     * @param joinId join Id
     * @return SignalInfo
     */
    public SignalInfo getBoolSignalInfo(int joinId) {
        return mBoolJoinSignalMap.get(joinId);
    }


    /**
     * Retrieves SignalInfo object using a boolean join id
     *
     * @param joinId join Id
     * @return SignalInfo
     */
    public SignalInfo getReservedBoolJoinInfo(int joinId){
        return mBooleanReservedJoinMap.get(joinId);
    }

    /**
     * Retrieves SignalInfo object using a serial join id
     *
     * @param joinId join Id
     * @return SignalInfo
     */
    public SignalInfo getReservedStringJoinInfo(int joinId){
        return mStringReservedJoinMap.get(joinId);
    }

    /**
     * Retrieves SignalInfo object using an analog join id
     *
     * @param joinId join Id
     * @return SignalInfo
     */
    public SignalInfo getReservedIntJoinInfo(int joinId){
        return mIntegerReservedJoinMap.get(joinId);
    }

    /**
     * Updates the boolean use case to reflect current state
     *
     * @param cipBooleanUseCase Boolean use case
     */
    public void updateBooleanUseCase(CIPBooleanUseCase cipBooleanUseCase) {
        mBoolSignalUseCaseMap.put(cipBooleanUseCase.getUseCaseName(), cipBooleanUseCase);
    }

    /**
     * Updates the string use case to reflect current state
     *
     * @param cipStringUseCase String use case
     */
    public void updateStringUseCase(CIPStringUseCase cipStringUseCase) {
        mStrSignalUseCaseMap.put(cipStringUseCase.getUseCaseName(), cipStringUseCase);
    }

    /**
     * Updates the integer use case to reflect current state
     *
     * @param cipIntegerUseCase Integer use case
     */
    public void updateIntegerUseCase(CIPIntegerUseCase cipIntegerUseCase) {
        mIntSignalUseCaseMap.put(cipIntegerUseCase.getUseCaseName(), cipIntegerUseCase);
    }



}
