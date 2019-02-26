package com.crestron.blackbird.mobile.projectmanagement.workers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crestron.blackbird.mobile.projectmanagement.net.Downloader;
import com.crestron.blackbird.mobile.projectmanagement.rx.RxProjectSaver;
import com.crestron.blackbird.mobile.projectmanagement.util.ProjectConstants;
import com.crestron.blackbird.mobile.projectmanagement.util.ProjectUtil;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.projectmanagement.DownloadProjectUseCase;

import java.io.IOException;
import java.util.Map;

import androidx.work.Worker;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Performs background work of downloading project from the network.
 */
public class DownloadWorker extends Worker {
    private static final String TAG = DownloadWorker.class.getSimpleName();
    private Context applicationContext;
    private String controlSysId = null;

    @NonNull
    @Override
    public WorkerResult doWork() {
        // Network response for downloading project
        Response<ResponseBody> response;
        try {
            Map<String, Object> inputMap = getInputData().getKeyValueMap();
            if (inputMap.containsKey(ProjectConstants.DATA_CONTROL_SYS_ID))
                controlSysId = String.valueOf(inputMap.get(ProjectConstants.DATA_CONTROL_SYS_ID));

            // Perform the download
            applicationContext = getApplicationContext();
            response = new Downloader(applicationContext).download(inputMap).execute();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return WorkerResult.FAILURE;
        }

        // Build a map with response data
        if (response.isSuccessful()) {
            handleSuccessfulResponse(response);
            return WorkerResult.SUCCESS;

            // Unsuccessful response from the network
        } else {
            handleUnsuccessfulResponse(response);
            return WorkerResult.FAILURE;
        }
    }

    /**
     * Take action on a net response in 200-300 range.
     *
     * @param response Response
     */
    private void handleSuccessfulResponse(Response<ResponseBody> response) {
        // Raw response from http client
        okhttp3.Response clientResponse = response.raw();

        // Raw response received from the network. Will be null if this response didn't use
        // the network, such as when the response is fully cached.
        okhttp3.Response netResponse = clientResponse.networkResponse();  //

        int code = 0;
        if (netResponse == null) {
            // Server doesn't support raw network response
            code = clientResponse.code();
        } else {
            // Code is raw network response
            code = netResponse.code();
        }

        switch (code) {
            case 200:
                // Save the response body in the filesystem and db
                RxProjectSaver rxProjectSaver = new RxProjectSaver(applicationContext, response.body(), controlSysId);
                rxProjectSaver.doRxSaveAndUnzip();
                break;

            case 304:
                // Verify existence of project in the filesystem and db
                ProjectUtil.checkDbAndFileSystem(applicationContext, controlSysId);
                break;

            default:
                Log.w(TAG, "WARNING: don't know how to handle this response "
                        + response.code()
                        + " " + response.message());
                break;
        }
    }

    private void handleUnsuccessfulResponse(Response<ResponseBody> response) {
        ResponseBody errorBody = response.errorBody();
        String message = String.format("Request failed %s ", errorBody.toString());
        Log.e(TAG, message);

        // Send error useCase to ui
        DownloadProjectUseCase useCase = new DownloadProjectUseCase();
        DownloadProjectUseCase.Response errorResponse = useCase.new Response();
        errorResponse.responseStatus = DownloadProjectUseCase.Status.FAILED;
        errorResponse.responseStatus.setStatusMessage(message);
        RxBus.INSTANCE.send(errorResponse);
    }

}