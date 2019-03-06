package com.crestron.mobile.bcip;

/**
 * <h1>CIPIntegerUseCaseResp class </h1>
 * <p>
 * Represents a CIP analog join response
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CIPIntegerUseCaseResp extends CIPIntegerUseCase {

    public CIPIntegerUseCaseResp(String useCaseName, int useCaseId, int controlJoinId) {
        super(useCaseName, useCaseId, controlJoinId);
    }

    public CIPIntegerUseCaseResp(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId, 0);
    }

    public CIPIntegerUseCaseResp(String useCaseName, int useCaseId, int controlJoinId, int value) {
        super(useCaseName, useCaseId, controlJoinId);
        this.value = value;
    }
}
