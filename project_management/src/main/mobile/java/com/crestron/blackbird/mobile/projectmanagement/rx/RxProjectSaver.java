package com.crestron.blackbird.mobile.projectmanagement.rx;

import android.content.Context;
import android.util.Log;

import com.crestron.blackbird.mobile.projectmanagement.interactor.DbInteractorProjMgt;
import com.crestron.blackbird.mobile.projectmanagement.model.ProjectEntry;
import com.crestron.blackbird.mobile.projectmanagement.util.FileHelper;
import com.crestron.blackbird.mobile.projectmanagement.util.ProjectConstants;
import com.crestron.blackbird.mobile.projectmanagement.util.ProjectUtil;
import com.crestron.itemattribute.db.tables.AttributeEntity;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.projectmanagement.DownloadProjectUseCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

/**
 * Performs saving + unzipping a project to the filesystem/db asynchrounously w/ RxJava.
 */
public class RxProjectSaver {
    private static final String TAG = RxProjectSaver.class.getSimpleName();

    private DbInteractorProjMgt databaseInteractor;
    private Context context;
    private ResponseBody responseBody;
    private String controlSysId;
    private File projectsDir;

    public RxProjectSaver(Context context, ResponseBody body, String controlSysId) {
        this.context = context;
        this.responseBody = body;
        this.controlSysId = controlSysId;
    }

    public void doRxSaveAndUnzip() {
        projectsDir = context.getDir(ProjectConstants.PROJECTS_DIR, Context.MODE_PRIVATE);
        databaseInteractor = new DbInteractorProjMgt(context);

        // Save and unzip to folder
        Observable saveObservable = save();
        saveObservable
                .subscribeOn(Schedulers.io())
                .subscribe(
                        /* Save success */
                        event -> unzip((File) event)
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                        /* Unzip success */
                                        hashCodeOfProject -> {
                                            ProjectEntry projectEntry = new ProjectEntry();
                                            projectEntry.setHash(hashCodeOfProject.toString());
                                            projectEntry.setControlSystemID(controlSysId);
                                            projectEntry.setLastAccessedDate(Calendar.getInstance().getTime().toString());
                                            createEntryInDb(projectEntry);

                                            DownloadProjectUseCase useCase = new DownloadProjectUseCase();
                                            DownloadProjectUseCase.Response successResponse = useCase.new Response();
                                            successResponse.responseStatus = DownloadProjectUseCase.Status.SUCCESS;
                                            successResponse.projectPath = projectsDir.getAbsolutePath() + File.separator + hashCodeOfProject;
                                            RxBus.INSTANCE.send(successResponse);
                                        },


                                        /* Unzip error */
                                        error -> {
                                            String message = String.format("Error in unzip: %s ", error);
                                            Log.e(TAG, message);
                                            // Do not save record in db, instead return error useCase to UI
                                            DownloadProjectUseCase useCase = new DownloadProjectUseCase();
                                            DownloadProjectUseCase.Response errorResponse = useCase.new Response();
                                            errorResponse.responseStatus = DownloadProjectUseCase.Status.FAILED;
                                            errorResponse.responseStatus.setStatusMessage(message);
                                            RxBus.INSTANCE.send(errorResponse);
                                        }),

                        /* Save error */
                        throwable -> {
                            String message = String.format("Received throwable from save: %s ", throwable.toString());
                            Log.e(TAG, message);
                            // Do not save record in db, instead return error useCase to UI
                            DownloadProjectUseCase useCase = new DownloadProjectUseCase();
                            DownloadProjectUseCase.Response errorResponse = useCase.new Response();
                            errorResponse.responseStatus = DownloadProjectUseCase.Status.FAILED;
                            errorResponse.responseStatus.setStatusMessage(message);
                            RxBus.INSTANCE.send(errorResponse);
                        });
    }

    /**
     * Save the responseBody to zip file.
     */
    private Observable save() {
        String tmpFileName = "DOWNLOADED";
        File tmpZipFile;

        try {
            // Create tmp zip file
            tmpZipFile = new File(projectsDir, tmpFileName + ".zip");

            BufferedSink sink = Okio.buffer(Okio.sink(tmpZipFile));
            sink.writeAll(responseBody.source());
            sink.close();
            Log.d(TAG, "File saved successfully to " + tmpZipFile.getAbsolutePath());
        } catch (IOException e) {
            return Observable.error(new Throwable(e.getMessage()));
        }

        return Observable.just(tmpZipFile);
    }


    /**
     * Unzip the downloaded file to bb_projects folder on the filesystem.
     *
     * @return String - path to the folder (MD5 hash) where project was unzipped
     */
    private Observable unzip(File downloadedZipFile) {
        Log.i(TAG, "In unzip() from downloaded file: " + downloadedZipFile.getAbsolutePath());
        String checkSumStr;
        File checkSumFile;

        // Step 1: calculate MD5 hash for DOWNLOADED.zip
        try (InputStream targetStream = new FileInputStream(downloadedZipFile)) {
            checkSumStr = ProjectUtil.getCheckSum(targetStream);
            checkSumFile = new File(projectsDir, checkSumStr + ".zip");
        } catch (IOException e) {
            return Observable.error(new Throwable(e.getMessage()));
        }

        // Step 2: Create a directory with the checksum name
        File unzipDirectory = new File(projectsDir, checkSumStr);
        unzipDirectory.mkdir();
        Log.d(TAG, "created directory: " + unzipDirectory.getAbsolutePath());

        // Step 3: Extract entries from DOWNLOADED.zip while creating required sub-directories
        try {
            FileHelper.extractZipTo(downloadedZipFile.getAbsolutePath(), unzipDirectory.getAbsolutePath());
        } catch (IOException e) {
            return Observable.error(new Throwable(e.getMessage()));
        }

        // Step 4: Delete DOWNLOADED.zip
        downloadedZipFile.delete();

        return Observable.just(checkSumStr);
    }

    /**
     * Create control sys entry.
     *
     * @param projectEntry ControlSystemEntry
     */
    private void createEntryInDb(ProjectEntry projectEntry) {
        databaseInteractor.createOrUpdateProjectEntry(projectEntry)
                .subscribeOn(Schedulers.io())
                .subscribe(new MaybeObserver<List<AttributeEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "createEntryInDb onSubscribe");
                    }

                    @Override
                    public void onSuccess(List<AttributeEntity> o) {
                        Log.i(TAG, "createEntryInDb onSuccess");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "createEntryInDb onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "createEntryInDb onComplete without results");
                    }
                });
    }

}