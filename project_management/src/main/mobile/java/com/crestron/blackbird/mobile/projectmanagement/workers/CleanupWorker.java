package com.crestron.blackbird.mobile.projectmanagement.workers;

import android.support.annotation.NonNull;

import com.crestron.blackbird.mobile.projectmanagement.util.ProjectConstants;

import java.io.File;

import androidx.work.Worker;

/**
 * Cleans up temporary files from the bb_projects folder.
 */
public class CleanupWorker extends Worker {
    //1)query db for last_accessed hashes

    //2)those older than 90 days get deleted

    @NonNull @Override public WorkerResult doWork() {
        File outputDirectory = new File(getApplicationContext().getFilesDir(), ProjectConstants.PROJECTS_DIR);

        return null;
    }
}
