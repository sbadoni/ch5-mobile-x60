package com.crestron.mobile.bcip;

import com.crestron.mobile.contract.UseCase;

/**
 * <h1>CIPUseCase base class </h1>
 * <p>
 * CIP use cases (joins) derive from this class
 *
 * @author Colm Coady
 * @version 1.0
 */

public abstract class CIPUseCase extends UseCase {

    /**
     * Control join id
     */
    private int controlJoinId;

    /**
     * @return control join id
     */
    public int getControlJoinId() {
        return controlJoinId;
    }

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

    CIPUseCase(String useCaseName, int useCaseId, int controlJoinId) {
        super(useCaseName, useCaseId);
        this.controlJoinId = controlJoinId;
    }

}
