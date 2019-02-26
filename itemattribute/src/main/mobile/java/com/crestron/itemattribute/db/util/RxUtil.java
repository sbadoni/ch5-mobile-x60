package com.crestron.itemattribute.db.util;

import android.annotation.SuppressLint;
import android.support.annotation.CheckResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Completable;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Helper class for RxJava interactions.
 */
public class RxUtil {

    /**
     * Contract to run a specific operation on the DB.
     */
    @FunctionalInterface
    public interface RunAction {
        void runOp();
    }

    /**
     * Runs operation for non {@link Maybe} RxJava actions.
     *
     * @param action the interface that will run the wanted action
     */
    public static void runDbOperation(RunAction action) {
        //TODO FOR FUTURE: See if there is a better background thread method to use
        ExecutorService executorService = Executors.newCachedThreadPool();//newSingleThreadExecutor();
        executorService.submit(action::runOp);
    }

    @FunctionalInterface
    public interface MaybeActions<M> {
        M maybeConvert();
    }

    /**
     * Wraps a generic in a {@link Maybe}.
     *
     * @param actions the interface that will run the wanted action
     * @return a {@link Maybe} version of a generic
     */
    @SuppressWarnings("unchecked")
    public static <M> M convertToRxMaybe(MaybeActions actions) {
        return (M) Maybe.create((MaybeOnSubscribe<M>) emitter -> {
            try {
                M obj = (M) actions.maybeConvert();
                if (obj != null) {
                    emitter.onSuccess(obj);
                } else {
                    emitter.onComplete();
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

}
