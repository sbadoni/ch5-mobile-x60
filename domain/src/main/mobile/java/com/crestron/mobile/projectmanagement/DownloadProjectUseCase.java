package com.crestron.mobile.projectmanagement;

import com.crestron.mobile.contract.UseCase;
import com.crestron.mobile.projectmanagement.model.ProjectForDownload;

/**
 * Command to download & save a project from control system.
 */
public class DownloadProjectUseCase extends UseCase {

    /**
     * Construct a use case instance with a random useCaseId.
     */
    public DownloadProjectUseCase() {
        super(DownloadProjectUseCase.class.getSimpleName(), (int) (Math.random()));
    }

    /**
     * UseCase inner Request class.
     * Typically used to make a request over the RxBus.
     */
    public class Request {
        public ProjectForDownload projectForDownload;
    }

    /**
     * UseCase inner Response class.
     * Typically used to handle a response from the RxBus.
     */
    public class Response {
        public Status responseStatus;
        public String projectPath;
    }

}
