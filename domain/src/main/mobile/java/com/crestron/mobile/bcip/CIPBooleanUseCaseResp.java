package com.crestron.mobile.bcip;

/**
 * <h1>CIPBooleanUseCaseResp class </h1>
 * <p>
 * Represents a CIP digital join response
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CIPBooleanUseCaseResp extends CIPBooleanUseCase {

    public CIPBooleanUseCaseResp(String useCaseName, int useCaseId, int controlJoinId) {
        super(useCaseName, useCaseId, controlJoinId);
    }

    public CIPBooleanUseCaseResp(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId, 0);
    }
}
