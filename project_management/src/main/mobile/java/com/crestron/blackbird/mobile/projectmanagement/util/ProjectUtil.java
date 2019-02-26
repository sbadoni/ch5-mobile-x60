package com.crestron.blackbird.mobile.projectmanagement.util;


import android.content.Context;
import android.util.Log;

import com.crestron.blackbird.mobile.projectmanagement.interactor.DbInteractorProjMgt;
import com.crestron.itemattribute.db.tables.AttributeEntity;
import com.crestron.itemattribute.db.tables.ItemEntity;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.projectmanagement.DownloadProjectUseCase;
import com.crestron.mobile.projectmanagement.RetryCacheProblemUseCase;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.TimeZone;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;

/**
 * General utility methods for project_management module.
 */
public final class ProjectUtil {
    public static final int CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final String TAG = ProjectUtil.class.getSimpleName();

    /*
     * Calculate the checksum of an input stream using MD5 algorithm.
     */
    public static String getCheckSum(InputStream path) {
        String checksumStr = null;
        try (DataInputStream fis = new DataInputStream(path)) {
            MessageDigest md = MessageDigest.getInstance("MD5");

            //Using MessageDigest update() method to provide input
            byte[] buffer = new byte[8192];
            int numOfBytesRead;
            while ((numOfBytesRead = fis.read(buffer)) > 0) {
                md.update(buffer, 0, numOfBytesRead);
            }
            byte[] hash = md.digest();
            checksumStr = new BigInteger(1, hash).toString(16);
        } catch (IOException | NoSuchAlgorithmException ex) {
            Log.e(TAG, ex.getMessage());
        }

        return checksumStr;
    }

    /**
     * Query db for hash entry, and if it is there - does it also exist on the file system.
     */
    public static void checkDbAndFileSystem(Context context, String sysId) {
        DbInteractorProjMgt databaseInteractor = new DbInteractorProjMgt(context);

        // Asynchronously do lookup in db
        Maybe<ItemEntity> maybeProject = databaseInteractor.getItemById(Long.valueOf(sysId));
        maybeProject
                .subscribeOn(Schedulers.io())
                .subscribe(new MaybeObserver<ItemEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "checkDbAndFileSystem onSubscribe");
                    }

                    @Override
                    public void onSuccess(ItemEntity item) {
                        Log.i(TAG, "checkDbAndFileSystem onSuccess");
                        File projectsParentDir = context.getDir(ProjectConstants.PROJECTS_DIR, Context.MODE_PRIVATE);
                        File savedProjectDir;
                        String hash = item.getItem_key();
                        savedProjectDir = new File(projectsParentDir.getAbsolutePath(), hash);

                        // No saved project dir on the system
                        if (!savedProjectDir.exists()) {
                            deleteProjFromDbAndCache(databaseInteractor, sysId, context);

                            // Saved project dir exists
                        } else {
                            // Send success response useCase to ui with hash folder location from db
                            DownloadProjectUseCase useCase = new DownloadProjectUseCase();
                            DownloadProjectUseCase.Response downloadUseCaseResponse = useCase.new Response();
                            downloadUseCaseResponse.projectPath = savedProjectDir.getAbsolutePath();
                            RxBus.INSTANCE.send(downloadUseCaseResponse);

                            // Update the timestamp in the db for this project
                            updateLastAccessedDateInDb(hash, sysId, databaseInteractor);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "checkDbAndFileSystem onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.w(TAG, "checkDbAndFileSystem onComplete without results");
                        // Project item doesn't exist in projects.db for this sysId.  Recreate it.
                        deleteProjFromDbAndCache(databaseInteractor, sysId, context);
                    }
                });
    }


    /**
     * Update the lastAccessedDate for this project in db.
     *
     * @param hash               String
     * @param sysId              String
     * @param databaseInteractor DbInteractorProjMgt
     */
    private static void updateLastAccessedDateInDb(String hash, String sysId, DbInteractorProjMgt databaseInteractor) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        AttributeEntity attributeEntry = new AttributeEntity();
        attributeEntry.attrKey = hash;
        attributeEntry.value = Calendar.getInstance(timeZone).getTime().toString();
        attributeEntry.attrID = Long.valueOf(sysId);
        databaseInteractor.updateLastAccessedDate(attributeEntry);
        io.reactivex.Completable updateDateInDb = databaseInteractor.updateLastAccessedDate(attributeEntry);
        updateDateInDb
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    /**
     * Delete entry from db, and clear the http cache.
     *
     * @param databaseInteractor DbInteractorProjMgt
     * @param sysId String
     * @param context Context
     */
    private static void deleteProjFromDbAndCache(DbInteractorProjMgt databaseInteractor, String sysId, Context context) {
        databaseInteractor.deleteProject(Long.valueOf(sysId));

        Cache cache = new Cache(context.getCacheDir(), CACHE_SIZE);
        try {
            cache.delete();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        // Now send useCase to restart the download
        RetryCacheProblemUseCase useCase = new RetryCacheProblemUseCase();
        RetryCacheProblemUseCase.Request useCaseRequest = useCase.new Request();
        RxBus.INSTANCE.send(useCaseRequest);
    }

}
