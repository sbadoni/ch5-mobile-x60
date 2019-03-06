package com.crestron.video.panel;

import android.content.Context;
import android.util.Log;

//import com.crestron.logging.Logger;
import com.crestron.mobile.android.common.RxBus;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


import com.crestron.mobile.bcip.reservedjoin.Csig_String_UseCase;
import com.crestron.mobile.bcip.reservedjoin.Csig_String_UseCaseResp;
import com.crestron.utils.Observable;

/**
 * Created by gdelarosa on 11/7/18.
 */

public class VideoManagerRx extends VideoManager{
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private VideoReponseObserver mVideoResponseObservor = null;

    public VideoManagerRx(Context context) {
        super(context);
        mVideoResponseObservor = new VideoReponseObserver();
    }

    @Override
    public void init() {
        super.init();
        mVideoSurfaceManager.getVideoResponseObservable().addObserver(mVideoResponseObservor);

        mCompositeDisposable.add(RxBus.INSTANCE
                .listen(Csig_String_UseCase.class)
                .subscribeOn(Schedulers.io())
                .subscribe(useCase -> {
                    mVideoSurfaceManager.acceptMessage(useCase.getValue());
                }));

    }

    private class VideoReponseObserver extends Observable.Observer<String>{
        @Override
        public void observe(String param) {
            Csig_String_UseCaseResp response = new Csig_String_UseCaseResp("Csig.video.response", 0);
            response.setValue(param);
            RxBus.INSTANCE.send(response);
        }
    }

    @Override
    public void deinit() {
        mCompositeDisposable.dispose();
        super.deinit();
    }
}
