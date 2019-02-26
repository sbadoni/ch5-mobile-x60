package com.crestron.mobile.bcip;

import com.crestron.mobile.contract.UseCase;

/**
 * <h1>CIPOpUseCase class </h1>
 * <p>
 * CIP use cases derive from this class
 *
 * @author Colm Coady
 * @version 1.0
 */

public abstract class CIPOpUseCase extends UseCase {

    /**
     * UseCase inner Request class
     * Typically used to make a request over the RxBus
     * Child classes may create an instance of this class
     */
    public class Request {

    }

    /**
     * UseCase inner Response class
     * Typically used to handle a response from the RxBus
     * Child classes may create an instance of this class
     */
    public class Response {

    }

    CIPOpUseCase(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);

    }


}
