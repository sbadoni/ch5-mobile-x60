package com.crestron.mobile.cssettings;

import com.crestron.mobile.contract.UseCase;

/**
 * <h1>CSPersistenceControlSystemEntryOpUseCase base class </h1>
 * <p>
 * Control system persistence use cases derive from this class
 *
 * @author Colm Coady
 * @version 1.0
 */
public abstract class CSPersistenceControlSystemEntryOpUseCase extends UseCase {

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
        public Status responseStatus;

    }

    CSPersistenceControlSystemEntryOpUseCase(String useCaseName) {
        super(useCaseName, 0);
    }
}
