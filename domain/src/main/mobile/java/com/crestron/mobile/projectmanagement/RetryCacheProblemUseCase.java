package com.crestron.mobile.projectmanagement;

import com.crestron.mobile.contract.UseCase;

/**
 * Command to re-start project download when there is a problem in the file system cache.
 */
public class RetryCacheProblemUseCase extends UseCase {

    /**
     * Construct a use case instance with a random useCaseId.
     */
    public RetryCacheProblemUseCase() {
        super(DownloadProjectUseCase.class.getSimpleName(), (int) (Math.random()));
    }

    /**
     * UseCase inner Request class.
     * Typically used to make a request over the RxBus.
     */
    public class Request {

    }

    /**
     * UseCase inner Response class.
     * Typically used to handle a response from the RxBus.
     */
    public class Response {
        public UseCase.Status responseStatus;
        public String projectUri;
    }

}
