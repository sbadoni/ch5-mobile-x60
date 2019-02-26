package com.crestron.mobile.bcip;

import com.crestron.cip.BcipConnectionInfo;
import com.crestron.cip.ConnectionMgrBlackbird;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.bcip.reservedjoin.Csig_Boolean_UseCase;
import com.crestron.mobile.bcip.reservedjoin.Csig_Integer_UseCase;
import com.crestron.mobile.bcip.reservedjoin.Csig_String_UseCase;
import com.crestron.mobile.bcip.reservedjoin.response.ReservedBooleanResponseUseCase;
import com.crestron.mobile.bcip.reservedjoin.response.ReservedIntegerResponseUseCase;
import com.crestron.mobile.bcip.reservedjoin.response.ReservedStringResponseUseCase;
import com.crestron.mobile.cssettings.model.ControlSystemEntry;
import com.crestron.mobile.projectmanagement.DownloadProjectUseCase;
import com.crestron.mobile.projectmanagement.model.ProjectForDownload;
import com.crestron.mobile.signal.SignalInfo;
import com.crestron.mobile.signal.SignalManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * <h1>CIPCSConnectionHandler class </h1>
 * <p>
 * Used to handle manage connection to control system
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CIPCSConnectionHandler {
    private static final String TAG = CIPCSConnectionHandler.class.getName();

    private ConnectionMgrBlackbird mConnectionMgr;
    private CIPHandler mCIPHandler;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private Boolean mConnected = false;
    private SignalManager mSignalManager = null;
    private ControlSystemEntry controlSystemEntry;

    public void init() {

        mSignalManager = SignalManager.getInstance();

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPConnectToCSUseCase.Request.class)
                .subscribeOn(Schedulers.io())
                .subscribe(request -> {
                    connectToControlSystem(request.getControlSystemEntry());
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPIntegerUseCase.class)
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        int joinId = mSignalManager.getIntegerUseCaseId(useCase.getUseCaseName());
                        if (joinId > -1)
                            mConnectionMgr.sendAnalogJoin(0, joinId, useCase.getValue());
                    }
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPStringUseCase.class)
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        int joinId = mSignalManager.getStringUseCaseId(useCase.getUseCaseName());
                        if (joinId > -1)
                            mConnectionMgr.sendSerialJoin(0, joinId, useCase.getValue());
                    }
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPBooleanUseCase.class)
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        int joinId = mSignalManager.getBoolUseCaseId(useCase.getUseCaseName());
                        if (joinId > -1)
                            mConnectionMgr.sendDigitalJoin(0, joinId, useCase.getValue());
                    }
                }));

        //All Reserved Serial(String) Joins will come here
        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_String_UseCase.class)
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        mConnectionMgr.sendSerialJoin(0, useCase.getUseCaseId(), useCase.getValue());
                    }
                }));

        //All Reserved Analog(Integer) Joins will come here
        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_Integer_UseCase.class)
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        mConnectionMgr.sendAnalogJoin(0, useCase.getUseCaseId(), useCase.getValue());
                    }
                }));
        //All Reserved Digital(Boolean) Joins will come here

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_Boolean_UseCase.class)
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    if (mConnected) {
                        if (useCase.getValue()) {
                            mConnectionMgr.toggleDigitalJoin(0, useCase.getUseCaseId(), useCase.getValue());
                        } else {
                            mConnectionMgr.sendDigitalJoin(0, useCase.getUseCaseId(), useCase.getValue());
                        }

                    }
                }));


    }

    public void deinit() {
        mCompositeDisposable.dispose();
    }

    private void connectToControlSystem(ControlSystemEntry controlSystemEntry) {
        if (mCIPHandler == null)
            mCIPHandler = new CIPHandler(this);

        if (mConnectionMgr == null)
            mConnectionMgr = com.crestron.cip.ConnectionMgrFactory.create(mCIPHandler, true);
        else
            mConnectionMgr.disconnect();

        if (mConnectionMgr != null) {
            this.controlSystemEntry = controlSystemEntry;
            List<BcipConnectionInfo> connInfo = new ArrayList<>();
            BcipConnectionInfo ci = new BcipConnectionInfo();
            ci.setConnectionName(controlSystemEntry.getFriendlyName());
            ci.setHostname(controlSystemEntry.getHostName1());
            ci.setIpidNumber(controlSystemEntry.getIpid());
            ci.setPortNumber(controlSystemEntry.getCipPort1());
            ci.setUseSsl(controlSystemEntry.isUseSSL());
            ci.setUserName(controlSystemEntry.getUserName());
            ci.setUserPassword(controlSystemEntry.getUserPassword());
            ci.setWebPortNumber(controlSystemEntry.getHttpPort1());

            connInfo.add(ci);
            mConnectionMgr.connect(connInfo);
        }

    }

    void updateUIState(int oldState, int newState) {
        if (oldState >= 11 && newState >= 11) {
            // Send status to UI
            mConnected = true;
            CIPConnectToCSUseCase.Response response = new CIPConnectToCSUseCase().new Response();
            response.setResponseStatus(CIPConnectToCSUseCase.Status.CONNECTED);
            RxBus.INSTANCE.send(response);
        }
    }

    /**
     * Send a use request to download and save the project from cs.
     *
     * @param bcipConnectionInfo String name of the project on the cs.
     */
    public void downloadProject(BcipConnectionInfo bcipConnectionInfo, String projectName) {
        // Build a bean to send to the downloader
        ProjectForDownload projectForDownload = new ProjectForDownload();
        projectForDownload.setControlSysID(controlSystemEntry.getControlSystemID());
        projectForDownload.setHostName(bcipConnectionInfo.getHostname());
        projectForDownload.setHttpPort(bcipConnectionInfo.getWebPortNumber());
        projectForDownload.setUserName(bcipConnectionInfo.getUserName());
        projectForDownload.setUserPassword(bcipConnectionInfo.getUserPassword());
        projectForDownload.setUseSSL(bcipConnectionInfo.isUseSsl());
        projectForDownload.setProjectName(projectName);

        // Send useCase to download proj
        DownloadProjectUseCase useCase = new DownloadProjectUseCase();
        DownloadProjectUseCase.Request request = useCase.new Request();
        request.projectForDownload = projectForDownload;
        RxBus.INSTANCE.send(request);
    }

    /**
     * Sends an boolean useCase response (from control system) to RxBus
     *
     * @param joinId    join id
     * @param joinValue join value
     */
    void sendDigitalChanged(int joinId, boolean joinValue) {

        if (mSignalManager != null) {
            SignalInfo signalInfo;
            signalInfo = mSignalManager.getBoolSignalInfo(joinId);
            if (signalInfo != null) {
                CIPBooleanUseCaseResp cipBoolUseCaseRp = new CIPBooleanUseCaseResp(signalInfo.getSignalName(), joinId);
                cipBoolUseCaseRp.setValue(joinValue);
                RxBus.INSTANCE.send(cipBoolUseCaseRp);
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
            SignalInfo signalInfo = mSignalManager.getIntegerSignalInfo(joinId);
            if (signalInfo != null) {
                CIPIntegerUseCaseResp cipIntUseCaseRp = new CIPIntegerUseCaseResp(signalInfo.getSignalName(), joinId);
                cipIntUseCaseRp.setValue(joinValue);
                RxBus.INSTANCE.send(cipIntUseCaseRp);
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
            SignalInfo signalInfo = mSignalManager.getStringSignalInfo(joinId);
            if (signalInfo != null) {
                CIPStringUseCaseResp cipStrUseCaseRp = new CIPStringUseCaseResp(signalInfo.getSignalName(), joinId);
                cipStrUseCaseRp.setValue(joinValue);
                RxBus.INSTANCE.send(cipStrUseCaseRp);
            }
        }
    }

    /**
     * Sends an boolean usecase response (from control system) to RxBus
     *
     * @param joinId    join id
     * @param joinValue join value
     */
    void sendReservedDigitalChanged(int joinId, boolean joinValue) {

        if (mSignalManager != null) {
            SignalInfo mReservedDigitalSignalInfo;
            mReservedDigitalSignalInfo = mSignalManager.getReservedBoolJoinInfo(joinId);
            if (mReservedDigitalSignalInfo != null) {
                ReservedBooleanResponseUseCase reservedDigitalUseCase =
                        new ReservedBooleanResponseUseCase(mReservedDigitalSignalInfo.getSignalName(), joinId);
                reservedDigitalUseCase.setValue(joinValue);
                RxBus.INSTANCE.send(reservedDigitalUseCase);
            }
        }
    }

    /**
     * Sends an integer usecase response (from control system) to RxBus
     *
     * @param joinId    join id
     * @param joinValue join value
     */
    void sendReservedAnalogChanged(int joinId, int joinValue) {

        if (mSignalManager != null) {
            SignalInfo mReservedAnalogSignalInfo;
            mReservedAnalogSignalInfo = mSignalManager.getReservedIntJoinInfo(joinId);
            if (mReservedAnalogSignalInfo != null) {
                ReservedIntegerResponseUseCase reservedAnalogUseCase =
                        new ReservedIntegerResponseUseCase(mReservedAnalogSignalInfo.getSignalName(), joinId);
                reservedAnalogUseCase.setValue(joinValue);
                RxBus.INSTANCE.send(reservedAnalogUseCase);
            }
        }
    }

    /**
     * Sends a string usecase response (from control system) to RxBus
     *
     * @param joinId    join id
     * @param joinValue join value
     */
    void sendReservedSerialChanged(int joinId, String joinValue) {
        if (mSignalManager != null) {
            SignalInfo mReservedSerialSignalInfo;
            mReservedSerialSignalInfo = mSignalManager.getReservedStringJoinInfo(joinId);
            if (mReservedSerialSignalInfo != null) {
                ReservedStringResponseUseCase reservedSerialUseCase =
                        new ReservedStringResponseUseCase(mReservedSerialSignalInfo.getSignalName(), joinId);
                reservedSerialUseCase.setValue(joinValue);
                RxBus.INSTANCE.send(reservedSerialUseCase);
            }
        }


    }
}
