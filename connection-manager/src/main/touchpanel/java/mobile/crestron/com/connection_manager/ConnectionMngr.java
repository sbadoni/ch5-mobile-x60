package mobile.crestron.com.connection_manager;

import android.content.Context;
import android.util.Log;

import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.bcip.CIPCSConnectionHandler;
import com.crestron.mobile.bcip.CIPConnectToIPLinkUseCase;
import com.crestron.mobile.bcip.CIPSendUpdateRequestUseCase;
import com.crestron.mobile.layout.ProjectReadyUseCase;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * <h1>ConnectionMngr class </h1>
 * <p>
 * Receives ready events from the relevant modules when they have fully loaded.
 * Once modules are loaded it will send and update request.
 *
 *
 * @author Colm Coady
 * @version 1.0
 */

public class ConnectionMngr {
    private static final String TAG = CIPCSConnectionHandler.class.getName();
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private CIPCSConnectionHandler mCIPCSConnectionHandler;
    private Context mApplicationContext;

    private boolean mProjectReady = false;
    private boolean mIPLinkConnected = false;

    public ConnectionMngr(Context mApplicationContext) {
        this.mApplicationContext = mApplicationContext;

        mCIPCSConnectionHandler = new CIPCSConnectionHandler( this.mApplicationContext );
        mCIPCSConnectionHandler.init();
    }

    public void init() {
        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(ProjectReadyUseCase.class)
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    mProjectReady = true;
                    Log.d( "","Project ready");
                    if (mProjectReady && mIPLinkConnected) {
                        Log.i("","Project ready and iplink connected.");
                        sendUpdateRequest();
                    }
                }));

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(CIPConnectToIPLinkUseCase.Response.class)
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    mIPLinkConnected = true;
                    Log.d( "","IP Link connected");
                    if (mProjectReady && mIPLinkConnected) {
                        Log.i("","Project ready and iplink connected.");
                        sendUpdateRequest();
                    }
                }));

    }

    public void deinit() {

        mCIPCSConnectionHandler.deinit();
        mCompositeDisposable.dispose();
    }


    /**
     * Sends a use case to get an update request
     */
    public void sendUpdateRequest() {

        CIPSendUpdateRequestUseCase sendUpdateReq = new CIPSendUpdateRequestUseCase();
        RxBus.INSTANCE.send(sendUpdateReq);
    }
}
