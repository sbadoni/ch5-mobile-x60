package com.crestron.blackbird.mobile.projectmanagement.workers;

import android.text.TextUtils;

import com.crestron.blackbird.mobile.projectmanagement.util.ProjectConstants;
import com.crestron.mobile.projectmanagement.model.ProjectForDownload;

import java.util.HashMap;
import java.util.Map;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

/**
 * Handles the logic creating WorkManager for project download.
 */
public class ProjectWorkManager {

    /**
     * Start work to download the project and save it to file system.
     *
     * @param projectForDownload ControlSystemEntry.
     */
    public void doWork(ProjectForDownload projectForDownload) {
        final WorkManager workManager = WorkManager.getInstance();

        // Create download constraint for network
        Constraints networkConstraint = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        // Add work request to download project
        OneTimeWorkRequest.Builder downloadBuilder = new OneTimeWorkRequest.Builder(DownloadWorker.class);
        downloadBuilder.setInputData(createInputDataForDownload(projectForDownload));
        downloadBuilder.setConstraints(networkConstraint);

        // Actually start the work
        if (workManager != null)
            workManager.enqueue(downloadBuilder.build());
    }

    /**
     * Creates an object to marshal to the worker.
     *
     * @param projectForDownload ControlSystemEntry
     * @return Data
     */
    private Data createInputDataForDownload(ProjectForDownload projectForDownload) {
        Data.Builder builder = new Data.Builder();
        Map<String, Object> map = new HashMap<String, Object>() {{
            if (!TextUtils.isEmpty(projectForDownload.getControlSysID()))
                put(ProjectConstants.DATA_CONTROL_SYS_ID, projectForDownload.getControlSysID());

            put(ProjectConstants.DATA_HOSTNAME, projectForDownload.getHostName());
            put(ProjectConstants.DATA_PORT, String.valueOf(projectForDownload.getHttpPort()));
            put(ProjectConstants.DATA_PROJECTNAME, projectForDownload.getProjectName());
            put(ProjectConstants.DATA_USERNAME, projectForDownload.getUserName());
            put(ProjectConstants.DATA_PASSWORD, projectForDownload.getUserPassword());
            put(ProjectConstants.DATA_USESSL, projectForDownload.getUseSSL());
        }};

        builder.putAll(map);

        return builder.build();
    }

}
