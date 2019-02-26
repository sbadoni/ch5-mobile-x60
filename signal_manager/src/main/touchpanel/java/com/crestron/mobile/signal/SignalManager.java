package com.crestron.mobile.signal;


import android.util.Log;

import com.crestron.mobile.bcip.CIPBooleanUseCase;
import com.crestron.mobile.bcip.CIPIntegerUseCase;
import com.crestron.mobile.bcip.CIPObjectUseCase;
import com.crestron.mobile.bcip.CIPStringUseCase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.crestron.mobile.signal.ReservedSignals.DIRECTION_BOTH;
import static com.crestron.mobile.signal.ReservedSignals.DIRECTION_TOUI;
import static com.crestron.mobile.signal.ReservedSignals.SIGNAL_TYPE_BOOL;
import static com.crestron.mobile.signal.ReservedSignals.SIGNAL_TYPE_INT;
import static com.crestron.mobile.signal.ReservedSignals.SIGNAL_TYPE_STRING;

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
     * Thread safe signal name, Object use case map
     */
    private ConcurrentHashMap<Object, SignalInfo> mObjJoinSignalMap = new ConcurrentHashMap<>();

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
     * Thread safe signal name, Object use case map
     */
    private ConcurrentHashMap<String, CIPObjectUseCase> mObjectSignalUseCaseMap = new ConcurrentHashMap<>();

    /*.................To UI.................*/
    /**
     * Thread safe signal name, Object use case map
     */
    private ConcurrentHashMap<Integer, SignalInfo> mIntReservedMapToUI = new ConcurrentHashMap<Integer, SignalInfo>();

    /*
     * Thread safe signal name, Object use case map
     */
    private ConcurrentHashMap<Integer, SignalInfo> mBoolReservedMapToUI = new ConcurrentHashMap<Integer, SignalInfo>();

    /**
     * Thread safe signal name, Object use case map
     */
    private ConcurrentHashMap<Integer, SignalInfo> mStrReservedMapToUI = new ConcurrentHashMap<Integer, SignalInfo>();

 /**
     *  Flag to indicate if user has specified signal json file
     */
    private boolean UserSuppliedSignals = false;

    public boolean isUserSignalsDefined() {
        return UserSuppliedSignals;
    }

    public ConcurrentHashMap<String, CIPBooleanUseCase> getBoolSignalUseCaseMap() {
        return mBoolSignalUseCaseMap;
    }

    public ConcurrentHashMap<String, CIPIntegerUseCase> getIntSignalUseCaseMap() {
        return mIntSignalUseCaseMap;
    }

    public ConcurrentHashMap<String, CIPStringUseCase> getStrSignalUseCaseMap() {
        return mStrSignalUseCaseMap;
    }


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
     * @param reservedInputStream of reserved signal JSON file
     */
    public void init(File userSignalJSONFile, InputStream reservedInputStream )
    {

        try {
            clean();
            SignalLoader signalLoader = new SignalLoader();
            if(userSignalJSONFile !=null)
            signalLoader.loadUserSignals(userSignalJSONFile);

            if (reservedInputStream != null)
                mapReservedJoins(signalLoader, reservedInputStream);

            if (signalLoader.getUserSignals() != null) {
                UserSuppliedSignals = true;
                // Add user joins coming from UI
                for (int i = 0; i < signalLoader.getUserSignals().string.outbound.size(); i++) {
                    SignalInfo signalInfo = signalLoader.getUserSignals().string.outbound.get(i);

                    CIPStringUseCase cipStringUseCase = null;

                    if (signalInfo.getJoinId() == 0) {
                        try {
                            int joinId = Integer.parseInt(signalInfo.getSignalName());
                            signalInfo.setJoinId((short)joinId);
                            cipStringUseCase = new CIPStringUseCase(signalInfo.getSignalName(), signalInfo.getJoinId(), signalInfo.getControlJoinId());

                        } catch (Exception e) {
                            Log.d( "Couldn't convert " , signalInfo.getSignalName());// signal name to number:
                        }
                    } else {
                        cipStringUseCase = new CIPStringUseCase(signalInfo.getSignalName(), signalInfo.getJoinId(), signalInfo.getControlJoinId());
                    }
                    if (cipStringUseCase != null)
                        mStrSignalUseCaseMap.put(signalInfo.getSignalName(), cipStringUseCase);
                }

                for (int i = 0; i < signalLoader.getUserSignals().booleanT.outbound.size(); i++) {
                    CIPBooleanUseCase cipBooleanUseCase = null;
                    SignalInfo signalInfo = signalLoader.getUserSignals().booleanT.outbound.get(i);

                    if (signalInfo.getJoinId() == 0) {
                        try {
                            int joinId = Integer.parseInt(signalInfo.getSignalName());
                            signalInfo.setJoinId((short)joinId);
                            cipBooleanUseCase = new CIPBooleanUseCase(signalInfo.getSignalName(), signalInfo.getJoinId(), signalInfo.getControlJoinId());

                        } catch (Exception e) {
                            Log.d( "Couldn't convert  " , signalInfo.getSignalName());//signal name to number:
                        }
                    } else {
                        cipBooleanUseCase = new CIPBooleanUseCase(signalInfo.getSignalName(), signalInfo.getJoinId(), signalInfo.getControlJoinId());
                    }
                    if (cipBooleanUseCase != null)
                        mBoolSignalUseCaseMap.put(signalInfo.getSignalName(), cipBooleanUseCase);
                }

                for (int i = 0; i < signalLoader.getUserSignals().numeric.outbound.size(); i++) {
                    SignalInfo signalInfo = signalLoader.getUserSignals().numeric.outbound.get(i);
                    CIPIntegerUseCase cipIntegerUseCase = null;
                    if (signalInfo.getJoinId() == 0) {
                        try {
                            int joinId = Integer.parseInt(signalInfo.getSignalName());
                            signalInfo.setJoinId((short)joinId);
                            cipIntegerUseCase = new CIPIntegerUseCase(signalInfo.getSignalName(), signalInfo.getJoinId(), signalInfo.getControlJoinId());

                        } catch (Exception e) {
                            Log.d( "Couldn't convert  " , signalInfo.getSignalName());//signal name to number:
                        }
                    } else {
                        cipIntegerUseCase = new CIPIntegerUseCase(signalInfo.getSignalName(), signalInfo.getJoinId(), signalInfo.getControlJoinId());
                    }
                    if (cipIntegerUseCase != null)
                        mIntSignalUseCaseMap.put(signalInfo.getSignalName(), cipIntegerUseCase);
                }

                // Add user joins coming from control system
                for (int i = 0; i < signalLoader.getUserSignals().string.inbound.size(); i++) {
                    if (signalLoader.getUserSignals().string.inbound.get(i).getJoinId() == 0)
                    {
                        String signalName = "";
                        try {
                            signalName = signalLoader.getUserSignals().string.inbound.get(i).getSignalName();
                            int joinId = Integer.parseInt(signalName);
                            signalLoader.getUserSignals().string.inbound.get(i).setSignalName("fb"+signalName);
                            mStrJoinSignalMap.put(joinId, signalLoader.getUserSignals().string.inbound.get(i));
                        } catch (Exception e) {
                            Log.d( "Couldn't convert  " , signalName);//signal name to number:
                        }
                    }
                    else
                    mStrJoinSignalMap.put((int) signalLoader.getUserSignals().string.inbound.get(i).getJoinId(), signalLoader.getUserSignals().string.inbound.get(i));
                }

                for (int i = 0; i < signalLoader.getUserSignals().booleanT.inbound.size(); i++) {
                    if (signalLoader.getUserSignals().booleanT.inbound.get(i).getJoinId() == 0)
                    {
                        String signalName = "";
                        try {
                            signalName = signalLoader.getUserSignals().booleanT.inbound.get(i).getSignalName();
                            int joinId = Integer.parseInt(signalName);
                            signalLoader.getUserSignals().booleanT.inbound.get(i).setSignalName("fb"+signalName);
                            mBoolJoinSignalMap.put(joinId, signalLoader.getUserSignals().booleanT.inbound.get(i));
                        } catch (Exception e) {

                            Log.d( "Couldn't convert: " , signalName);// signal name to number
                        }
                    }
                    else
                        mBoolJoinSignalMap.put((int) signalLoader.getUserSignals().booleanT.inbound.get(i).getJoinId(), signalLoader.getUserSignals().booleanT.inbound.get(i));
                }

                for (int i = 0; i < signalLoader.getUserSignals().numeric.inbound.size(); i++) {
                    if (signalLoader.getUserSignals().numeric.inbound.get(i).getJoinId() == 0)
                    {
                        String signalName = "";
                        try {
                            signalName = signalLoader.getUserSignals().numeric.inbound.get(i).getSignalName();
                            int joinId = Integer.parseInt(signalName);
                            signalLoader.getUserSignals().numeric.inbound.get(i).setSignalName("fb"+signalName);
                            mIntJoinSignalMap.put(joinId, signalLoader.getUserSignals().numeric.inbound.get(i));
                        } catch (Exception e) {
                            Log.d( "Couldn't convert  " , signalName);//signal name to number:
                        }
                    }
                    else
                        mIntJoinSignalMap.put((int) signalLoader.getUserSignals().numeric.inbound.get(i).getJoinId(), signalLoader.getUserSignals().numeric.inbound.get(i));
                }
            }

        }
        catch (Exception e)
        {
            Log.e( "Error e: " , e.toString());
        }

    }

    /**
     * @param signalLoader
     * @param reservedInputStream
     */
    public void mapReservedJoins(SignalLoader signalLoader, InputStream reservedInputStream) throws IOException {

        signalLoader.loadReservedSignals(reservedInputStream);
        ReservedSignals reservedSignals = signalLoader.getReservedSignals();
        List<SignalInfo> reservedSignalInfoList = reservedSignals.getSignals();
        if (reservedSignals != null && reservedSignalInfoList != null) {
            for (int i = 0; i < reservedSignalInfoList.size(); i++) {
                SignalInfo signalInfo = reservedSignalInfoList.get(i);
                //To UI or both
                if (reservedSignalInfoList.get(i).getDirection().equals(DIRECTION_TOUI) || reservedSignalInfoList.get(i).getDirection().equals(DIRECTION_BOTH)) {
                    if (signalInfo.getType().equals(SIGNAL_TYPE_BOOL)) {
                        mBoolReservedMapToUI.put((int) signalInfo.getJoinId(), signalInfo);
                    } else if (signalInfo.getType().equals(SIGNAL_TYPE_INT)) {
                        mIntReservedMapToUI.put((int) signalInfo.getJoinId(), signalInfo);
                    } else if (signalInfo.getType().equals(SIGNAL_TYPE_STRING)) {
                        mStrReservedMapToUI.put((int) signalInfo.getJoinId(), signalInfo);
                    }
                }
            }
        }
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

    public int getObjectUseCaseId(String signalName) {
        CIPObjectUseCase cipObjUseCase = mObjectSignalUseCaseMap.get(signalName);
        if (cipObjUseCase != null)
            return cipObjUseCase.getUseCaseId();
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


    /**
     * Adds boolean signal to relevant maps
     *
     * @param signalname signal name
     * @param signalId signal id
     */
    public void addBooleanSignal(String signalname, int signalId) {

        SignalInfo signalInfo =  mBoolJoinSignalMap.get(signalId);
        if (signalInfo == null)
        {
            signalInfo = new SignalInfo();
            signalInfo.setJoinId((short)signalId);
            signalInfo.setSignalName(signalname);

            mBoolJoinSignalMap.put(signalId, signalInfo);
        }
        CIPBooleanUseCase cipBooleanUseCase =  mBoolSignalUseCaseMap.get(signalname);
        if (cipBooleanUseCase == null)
        {
            cipBooleanUseCase = new CIPBooleanUseCase(signalname, signalId, 0);
            mBoolSignalUseCaseMap.put(signalInfo.getSignalName(), cipBooleanUseCase);
        }

    }


    /**
     * Adds integer signal to relevant maps
     *
     * @param signalname signal name
     * @param signalId signal id
     */
    public void addStringSignal(String signalname, int signalId) {

        SignalInfo signalInfo =  mStrJoinSignalMap.get(signalId);
        if (signalInfo == null)
        {
            signalInfo = new SignalInfo();
            signalInfo.setJoinId((short)signalId);
            signalInfo.setSignalName(signalname);

            mStrJoinSignalMap.put(signalId, signalInfo);
        }
        CIPStringUseCase cipStringUseCase =  mStrSignalUseCaseMap.get(signalname);
        if (cipStringUseCase == null)
        {
            cipStringUseCase = new CIPStringUseCase(signalname, signalId, 0);
            mStrSignalUseCaseMap.put(signalInfo.getSignalName(), cipStringUseCase);
        }

    }

    /**
     * Adds string integer to relevant maps
     *
     * @param signalname signal name
     * @param signalId signal id
     */
    public void addIntegerSignal(String signalname, int signalId) {

        SignalInfo signalInfo =  mIntJoinSignalMap.get(signalId);
        if (signalInfo == null)
        {
            signalInfo = new SignalInfo();
            signalInfo.setJoinId((short)signalId);
            signalInfo.setSignalName(signalname);

            mIntJoinSignalMap.put(signalId, signalInfo);
        }
        CIPIntegerUseCase cipIntegerUseCase =  mIntSignalUseCaseMap.get(signalname);
        if (cipIntegerUseCase == null)
        {
            cipIntegerUseCase = new CIPIntegerUseCase(signalname, signalId, 0);
            mIntSignalUseCaseMap.put(signalInfo.getSignalName(), cipIntegerUseCase);
        }

    }

    /*..............Reserved......ToUI................*/
    /**
     * Retrieves SignalInfo object using Boolean joinId
     *
     * @param joinId join Id
     * @return SignalInfo
     */
    public SignalInfo getBoolReservedSignalInfoToUI(int joinId) {
        return mBoolReservedMapToUI.get(joinId);
    }
    /**
     * Retrieves SignalInfo object using Integer joinId
     *
     * @param joinId join Id
     * @return SignalInfo
     */
    public SignalInfo getIntReservedSignalInfoToUI(int joinId) {
        return mIntReservedMapToUI.get(joinId);
    }

    /**
     * Retrieves SignalInfo object using String joinId
     *
     * @param joinId join Id
     * @return SignalInfo
     */
    public SignalInfo getStrReservedSignalInfoToUI(int joinId) {
        return mStrReservedMapToUI.get(joinId);
    }

    /**
     * Get Reserved Signal Use Case from Signal Name
     *
     * @param signalName
     * @param fromUI
     * @return
     */
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

    /**
     *
     */
    private void clean() {
        mStrSignalUseCaseMap.clear();
        mBoolSignalUseCaseMap.clear();
        mIntSignalUseCaseMap.clear();
        mStrJoinSignalMap.clear();
        mObjectSignalUseCaseMap.clear();
        mStrReservedMapToUI.clear();
        mIntReservedMapToUI.clear();
        mBoolReservedMapToUI.clear();
    }
}
