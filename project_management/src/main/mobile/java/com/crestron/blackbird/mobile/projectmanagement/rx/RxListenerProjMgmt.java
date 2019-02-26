package com.crestron.blackbird.mobile.projectmanagement.rx;

import android.support.annotation.VisibleForTesting;

import com.crestron.blackbird.mobile.projectmanagement.workers.ProjectWorkManager;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.android.common.rx.RxListenerBase;
import com.crestron.mobile.projectmanagement.DownloadProjectUseCase;
import com.crestron.mobile.projectmanagement.RetryCacheProblemUseCase;
import com.crestron.mobile.projectmanagement.model.ProjectForDownload;

/**
 * Listens on RxBus for useCases to project management layer.
 */
public class RxListenerProjMgmt implements RxListenerBase {
    private ProjectForDownload projectForDownload;

    /**
     * Listen on events from RxBus.
     */
    @Override
    public void startListening() {
        //Download proj upon connection response from cs
        RxBus.INSTANCE
                .listen(DownloadProjectUseCase.Request.class)
                .subscribe(request -> {
                    this.projectForDownload = request.projectForDownload;
                    downloadProject(request.projectForDownload);
                });

        //Retry download proj when there is a problem
        RxBus.INSTANCE
                .listen(RetryCacheProblemUseCase.Request.class)
                .subscribe(request -> {
                    downloadProject(projectForDownload);
                });

    }

    @VisibleForTesting
    public void downloadProject(ProjectForDownload projectForDownload) {
        // Send in progress status
        DownloadProjectUseCase useCase = new DownloadProjectUseCase();
        DownloadProjectUseCase.Response response = useCase.new Response();
        response.responseStatus = DownloadProjectUseCase.Status.STARTED;
        RxBus.INSTANCE.send(response);

        // Do the work
        ProjectWorkManager projectWorkManager = new ProjectWorkManager();
        projectWorkManager.doWork(projectForDownload);
    }

}