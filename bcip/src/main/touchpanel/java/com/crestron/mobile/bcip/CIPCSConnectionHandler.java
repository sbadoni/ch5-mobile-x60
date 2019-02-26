package com.crestron.mobile.bcip;

import android.content.Context;
import android.util.Log;

import com.crestron.cip.ConnectionMgrIplink;
import com.crestron.cip.IplinkClientInterface;
import com.crestron.cresstore.CresStoreLibraryNotFoundException;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.layout.ProjectReadyUseCase;
import com.crestron.mobile.reservedjoin.Csig_Boolean_UseCase;
import com.crestron.mobile.reservedjoin.Csig_Boolean_UseCaseResp;
import com.crestron.mobile.reservedjoin.Csig_Integer_UseCase;
import com.crestron.mobile.reservedjoin.Csig_Integer_UseCaseResp;
import com.crestron.mobile.reservedjoin.Csig_String_UseCase;
import com.crestron.mobile.reservedjoin.Csig_String_UseCaseResp;

import com.crestron.mobile.reservedjoin.*;

import com.crestron.mobile.signal.SignalInfo;
import com.crestron.mobile.signal.SignalManager;
import com.crestron.secure_storage.SecureStorageLibraryNotFoundException;
import com.crestron.signal_transport.SignalTransportFactory;
import com.crestron.signal_transport.SignalTransportInterface;

import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * <h1>CIPCSConnectionHandler class </h1>
 * <p>
 * Used to handle manage connection to control system via firmware
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CIPCSConnectionHandler {
    private static final String TAG = CIPCSConnectionHandler.class.getName();
    private IplinkClientInterface mIplinkClientInterface;
    private SignalTransportInterface mSignalTransport;
    private CIPHandler mCIPHandler;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private Boolean mConnected = false;
    private Boolean mProjectReady = true;
    private SignalManager mSignalManager = null;
    Context mApplicationContext;
    boolean ENABLE_IPLINK_LOGGING = true;
    private int mIpLinkPort;

    public CIPCSConnectionHandler(Context mApplicationContext) {
        this.mApplicationContext = mApplicationContext;
    }

    public void init() {

        mSignalManager = SignalManager.getInstance();


        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPConnectToIPLinkUseCase.Request.class).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    instantiateSignalTransport(useCase);
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPSendUpdateRequestUseCase.class).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    sendUpdateRequest();
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPIntegerUseCase.class).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        // user json defined - lookup id
                        int joinId = mSignalManager.getIntegerUseCaseId(useCase.getUseCaseName());
                        if (joinId == -1)
                            joinId = convertToNumber(useCase.getUseCaseName());
                        if (joinId > -1) {
                            Log.v( "sendAnalogJoin: " , joinId + " / " + String.valueOf(useCase.getValue()));
                            mIplinkClientInterface.sendAnalogJoin(0, joinId, useCase.getValue());
                        }
                    }
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPStringUseCase.class).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        int joinId = mSignalManager.getStringUseCaseId(useCase.getUseCaseName());
                        if (joinId == -1)
                            joinId = convertToNumber(useCase.getUseCaseName());

                        if (joinId > -1) {
                            Log.v( "sendSerialJoin: " , joinId + " / " + String.valueOf(useCase.getValue()));
                            mIplinkClientInterface.sendSerialJoin(0, joinId, useCase.getValue());
                        }
                    }
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPBooleanUseCase.class).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        int joinId = mSignalManager.getBoolUseCaseId(useCase.getUseCaseName());
                        if (joinId == -1)
                            joinId = convertToNumber(useCase.getUseCaseName());
                        if (joinId > -1) {
                            Log.v( "sendDigitalJoin: " , joinId + " / " + String.valueOf(useCase.getValue()));
                            mIplinkClientInterface.sendDigitalJoin(0, joinId, useCase.getValue());
                        }
                    }
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPObjectUseCase.class).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        int joinId = mSignalManager.getObjectUseCaseId(useCase.getUseCaseName());
                        if (joinId > -1)
                            mSignalTransport.sendSignal(useCase.getUseCaseName(), useCase.getValue());
                    }
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_Boolean_UseCase.class).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        Log.v( "sendDigitalJoin Reserved: " , useCase.getUseCaseId() + " / " + String.valueOf(useCase.getValue()));
                        mIplinkClientInterface.sendDigitalJoin(0, useCase.getUseCaseId(), useCase.getValue());
                    }
                }));
        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_Integer_UseCase.class).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        Log.v( "sendAnalogJoin Reser " , useCase.getUseCaseId() + " / " + String.valueOf(useCase.getValue()));
                        mIplinkClientInterface.sendAnalogJoin(0, useCase.getUseCaseId(), useCase.getValue());
                    }
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_String_UseCase.class).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        Log.v( "sendSerialJoin Res" , useCase.getUseCaseId() + " / " + String.valueOf(useCase.getValue()));
                        mIplinkClientInterface.sendSerialJoin(0, useCase.getUseCaseId(), useCase.getValue());
                    }
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(ProjectReadyUseCase.class)
                .subscribe(useCase -> {
                    mProjectReady = true;
                }));
    }

    public void deinit() {
        mCompositeDisposable.dispose();
    }

    private int convertToNumber(String value) {
        int num = -1;
        try {
            num = Integer.parseInt(value);
        } catch (Exception e) {
        }

        return num;
    }


    /**
     * Start Connection to IP Link and CresStore through Signal Transport
     *
     * @param cipConnectToCSUseCaseReq connect to ip link use case request
     *
     */
    protected void instantiateSignalTransport(CIPConnectToIPLinkUseCase.Request cipConnectToCSUseCaseReq) {
        try {
            Log.d( "instantiate" , cipConnectToCSUseCaseReq.getIPLinkPort()+"");

            if (cipConnectToCSUseCaseReq.getIPLinkPort() > 0)
                mSignalTransport = SignalTransportFactory.createSignalTransport(mApplicationContext, cipConnectToCSUseCaseReq.getIPLinkPort(), ENABLE_IPLINK_LOGGING, false, false);
            else
                mSignalTransport = SignalTransportFactory.createSignalTransport(mApplicationContext, ConnectionMgrIplink.DEFAULT_IPLINK_PORT, ENABLE_IPLINK_LOGGING, false, false);

            mIplinkClientInterface = (IplinkClientInterface) mSignalTransport;
            if (mCIPHandler == null) {
                mCIPHandler = new CIPHandler(this);
                mIplinkClientInterface.registerIplinkHandler(mCIPHandler);
                mConnected = true;

                Log.i("Connected over iplink: ",cipConnectToCSUseCaseReq.getIPLinkPort()+"");
                CIPConnectToIPLinkUseCase.Response connectResp = new CIPConnectToIPLinkUseCase().new Response();
                connectResp.setResponseStatus(CIPConnectToIPLinkUseCase.Status.CONNECTED);
                RxBus.INSTANCE.send(connectResp);
            }

        } catch (CresStoreLibraryNotFoundException e) {
            Log.e( "CresStore Not ","");
        } catch (SecureStorageLibraryNotFoundException e) {
            Log.e( "Secure Storage Library ","");
        }
    }

    /**
     * Request update request from iplink
     */
    public void sendUpdateRequest() {
        Log.i("sendUpdateRequest ","");
        mIplinkClientInterface.sendUpdateRequest(0);
    }

    /**
     * Prepares and sends a boolean use case to RxBus
     *
     * @param useCaseName join use case name
     * @param joinId      join id
     * @param joinValue   join value
     */
    private void sendBooleanUseCase(String useCaseName, int joinId, boolean joinValue) {
        CIPBooleanUseCaseResp cipBoolUseCaseRp = new CIPBooleanUseCaseResp(useCaseName, joinId);
        cipBoolUseCaseRp.setValue(joinValue);
        RxBus.INSTANCE.send(cipBoolUseCaseRp);
    }

    /**
     * Prepares and sends an integer use case to RxBus
     *
     * @param useCaseName join use case name
     * @param joinId      join id
     * @param joinValue   join value
     */
    private void sendIntegerUseCase(String useCaseName, int joinId, int joinValue) {
        CIPIntegerUseCaseResp cipIntUseCaseRp = new CIPIntegerUseCaseResp(useCaseName, joinId);
        cipIntUseCaseRp.setValue(joinValue);
        RxBus.INSTANCE.send(cipIntUseCaseRp);
    }

    /**
     * Prepares and sends a string use case to RxBus
     *
     * @param useCaseName join use case name
     * @param joinId      join id
     * @param joinValue   join value
     */
    private void sendStringUseCase(String useCaseName, int joinId, String joinValue) {
        CIPStringUseCaseResp cipStrUseCaseRp = new CIPStringUseCaseResp(useCaseName, joinId);
        cipStrUseCaseRp.setValue(joinValue);
        RxBus.INSTANCE.send(cipStrUseCaseRp);
    }

    /**
     * Sends an boolean usecase response (from control system) to RxBus
     *
     * @param joinId    join id
     * @param joinValue join value
     */
    void sendDigitalChanged(int joinId, boolean joinValue) {

        if (mSignalManager != null) {
            SignalInfo signalInfo = null;

            if (joinId < 17000) {
                if (!mSignalManager.isUserSignalsDefined()) { // pass user joins (if not defined) as fbjoinId use case
                    signalInfo = new SignalInfo();
                    signalInfo.setSignalName("fb" + joinId);
                } else {
                    signalInfo = mSignalManager.getBoolSignalInfo(joinId);
                    if (signalInfo != null) {
                        mSignalManager.updateBooleanUseCase(new CIPBooleanUseCase(signalInfo.getSignalName(), joinId, 0, joinValue));
                    }
                }
                if (mProjectReady && signalInfo != null) {
                    sendBooleanUseCase(signalInfo.getSignalName(), joinId, joinValue);
                }
            } else { // send reserved join use case
                signalInfo = mSignalManager.getBoolReservedSignalInfoToUI(joinId);
                if (signalInfo != null) {
                    Log.v( "reservedSignalInfo: " , signalInfo.getSignalName());
                    Object obj = mSignalManager.getReservedSignalUseCase(signalInfo.getSignalName(), false);
                    if (obj != null) {
                        Csig_Boolean_UseCaseResp csig_boolean_useCaseResp = (Csig_Boolean_UseCaseResp) obj;
                        csig_boolean_useCaseResp.setValue(joinValue);
                        if (mProjectReady)
                            RxBus.INSTANCE.send(csig_boolean_useCaseResp);
                    }
                }
            }
        }

    }


    /**
     * Sends an integer usecase response (from control system) to RxBus
     *
     * @param joinId    join id
     * @param joinValue join value
     */
    void sendAnalogChanged(int joinId, int joinValue) {

        if (mSignalManager != null) {
            SignalInfo signalInfo = null;
            if (joinId < 17000) {
                if (mSignalManager.isUserSignalsDefined()) { // pass user joins (if not defined) as fbjoinId use case
                    signalInfo = mSignalManager.getIntegerSignalInfo(joinId);
                    if (signalInfo != null) {
                        mSignalManager.updateIntegerUseCase(new CIPIntegerUseCase(signalInfo.getSignalName(), joinId, 0, joinValue));
                    }
                } else {
                    signalInfo = new SignalInfo();
                    signalInfo.setSignalName("fb" + joinId);
                }

                if (mProjectReady && signalInfo != null) {
                    sendIntegerUseCase(signalInfo.getSignalName(), joinId, joinValue);
                }
            } else { // send reserved join use case
                signalInfo = mSignalManager.getIntReservedSignalInfoToUI(joinId);
                if (signalInfo != null) {
                    Log.v( "reservedSignalInfo: " , signalInfo.getSignalName());
                    Object obj = mSignalManager.getReservedSignalUseCase(signalInfo.getSignalName(), false);
                    if (obj != null) {
                        Csig_Integer_UseCaseResp csig_integer_useCaseResp = (Csig_Integer_UseCaseResp) obj;
                        csig_integer_useCaseResp.setValue(joinValue);
                        if (mProjectReady)
                            RxBus.INSTANCE.send(csig_integer_useCaseResp);
                    }
                }

            }
        }
    }


    /**
     * Sends a string usecase response (from control system) to RxBus
     *
     * @param joinId    join id
     * @param joinValue join value
     */
    void sendSerialChanged(int joinId, String joinValue) {
        if (mSignalManager != null) {
            SignalInfo signalInfo = null;
            if (joinId < 17000) {
                if (mSignalManager.isUserSignalsDefined()) { // pass user joins (if not defined) as fbjoinId use case
                    signalInfo = mSignalManager.getStringSignalInfo(joinId);
                    if (signalInfo != null) {
                        mSignalManager.updateStringUseCase(new CIPStringUseCase(signalInfo.getSignalName(), joinId, 0, joinValue));
                    }
                } else {
                    signalInfo = new SignalInfo();
                    signalInfo.setSignalName("fb" + joinId);
                }

                if (mProjectReady && signalInfo != null) {
                    sendStringUseCase(signalInfo.getSignalName(), joinId, joinValue);
                }

            } else { // send reserved join use case
                signalInfo = mSignalManager.getStrReservedSignalInfoToUI(joinId);
                if (signalInfo != null) {
                    Log.v( "reservedSignalInfo: " , signalInfo.getSignalName());
                    Object obj = mSignalManager.getReservedSignalUseCase(signalInfo.getSignalName(), false);
                    if (obj != null) {
                        Csig_String_UseCaseResp csig_string_useCaseResp = (Csig_String_UseCaseResp) obj;
                        csig_string_useCaseResp.setValue(joinValue);
                        if (mProjectReady)
                            RxBus.INSTANCE.send(csig_string_useCaseResp);
                    }
                }
            }


        }
    }


    /**
     * Sends a Object use-case response (from control system) to RxBus
     *
     * @param signalName signal Name
     * @param joinValue  join value
     */
    void sendObjectSignalChange(String signalName, String joinValue) {
        if (mSignalManager != null) {
            int joinId;
            joinId = mSignalManager.getObjectUseCaseId(signalName);
            if (joinId != -1) {
                CIPObjectUseCaseResp cipObjUseCaseRp = new CIPObjectUseCaseResp(signalName, joinId);
                cipObjUseCaseRp.setJsonString(joinValue);
                RxBus.INSTANCE.send(cipObjUseCaseRp);
            }
        }
    }

    /**
     * Sends a Int Reserved use-case response (from control system) to RxBus
     *
     * @param joinId
     * @param joinValue
     */
    void sendIntReservedSignalChange(int joinId, int joinValue) {
        SignalInfo signalInfo = mSignalManager.getBoolReservedSignalInfoToUI(joinId);
        if (signalInfo != null) {
            Csig_Integer_UseCaseResp csig_integer_useCaseResp = (Csig_Integer_UseCaseResp) mSignalManager.getReservedSignalUseCase(signalInfo.getSignalName(), false);
            csig_integer_useCaseResp.setValue(joinValue);
            RxBus.INSTANCE.send(csig_integer_useCaseResp);
        }
    }

    /**
     * Sends a Int Reserved use-case response (from control system) to RxBus
     *
     * @param joinId
     * @param joinValue
     */
    void sendBoolReservedSignalChange(int joinId, boolean joinValue) {
        SignalInfo signalInfo = mSignalManager.getBoolReservedSignalInfoToUI(joinId);
        if (signalInfo != null) {
            Csig_Boolean_UseCaseResp csig_boolean_useCaseResp = (Csig_Boolean_UseCaseResp) mSignalManager.getReservedSignalUseCase(signalInfo.getSignalName(), false);
            csig_boolean_useCaseResp.setValue(joinValue);
            RxBus.INSTANCE.send(csig_boolean_useCaseResp);
        }
    }

    /**
     * Sends a Int Reserved use-case response (from control system) to RxBus
     *
     * @param joinId
     * @param joinValue
     */
    void sendStrReservedSignalChange(int joinId, String joinValue) {
        SignalInfo signalInfo = mSignalManager.getStrReservedSignalInfoToUI(joinId);
        if (signalInfo != null) {
            Csig_String_UseCaseResp csig_string_useCaseResp = (Csig_String_UseCaseResp) mSignalManager.getReservedSignalUseCase(signalInfo.getSignalName(), false);
            csig_string_useCaseResp.setValue(joinValue);
            RxBus.INSTANCE.send(csig_string_useCaseResp);
        }
    }

    /**
     * Sends all cip use cases to RxBus
     */
    void sendCIPUseCaseUpdates() {

        ConcurrentHashMap<String, CIPBooleanUseCase> BoolJoinSignalMap = mSignalManager.getBoolSignalUseCaseMap();

        while (BoolJoinSignalMap.entrySet().iterator().hasNext()) {
            ConcurrentHashMap.Entry<String, CIPBooleanUseCase> entry = BoolJoinSignalMap.entrySet().iterator().next();

            CIPBooleanUseCase booleanUseCase = entry.getValue();
            CIPBooleanUseCaseResp cipBoolUseCaseRp = new CIPBooleanUseCaseResp(booleanUseCase.getUseCaseName(), booleanUseCase.getUseCaseId(), booleanUseCase.getControlJoinId(), booleanUseCase.getValue());
            RxBus.INSTANCE.send(cipBoolUseCaseRp);
        }

        ConcurrentHashMap<String, CIPIntegerUseCase> IntSignalUseCaseMap = mSignalManager.getIntSignalUseCaseMap();

        while (IntSignalUseCaseMap.entrySet().iterator().hasNext()) {
            ConcurrentHashMap.Entry<String, CIPIntegerUseCase> entry = IntSignalUseCaseMap.entrySet().iterator().next();

            CIPIntegerUseCase integerUseCaseCase = entry.getValue();
            CIPIntegerUseCaseResp cipIntUseCaseCaseRp = new CIPIntegerUseCaseResp(integerUseCaseCase.getUseCaseName(), integerUseCaseCase.getUseCaseId(), integerUseCaseCase.getControlJoinId(), integerUseCaseCase.getValue());
            RxBus.INSTANCE.send(cipIntUseCaseCaseRp);
        }

        ConcurrentHashMap<String, CIPStringUseCase> StrSignalUseCaseMap = mSignalManager.getStrSignalUseCaseMap();

        while (StrSignalUseCaseMap.entrySet().iterator().hasNext()) {
            ConcurrentHashMap.Entry<String, CIPStringUseCase> entry = StrSignalUseCaseMap.entrySet().iterator().next();

            CIPStringUseCase stringUseCaseCase = entry.getValue();
            CIPStringUseCaseResp cipStrUseCaseCaseRp = new CIPStringUseCaseResp(stringUseCaseCase.getUseCaseName(), stringUseCaseCase.getUseCaseId(), stringUseCaseCase.getControlJoinId(), stringUseCaseCase.getValue());
            RxBus.INSTANCE.send(cipStrUseCaseCaseRp);
        }
    }
}
