package com.crestron.mobile.bcip;

/**
 * <h1>CIPBooleanUseCaseResp class </h1>
 * <p>
 * Represents a CIP digital join response
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CIPBooleanUseCaseResp extends  CIPUseCase {

    /**
     * Digital value
     */
    private boolean value;

    /**
     * @return Digital value
     */
    public boolean getValue() {
        return value;
    }

    /**
     * @param Digital value
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    public CIPBooleanUseCaseResp(String useCaseName, int useCaseId, int controlJoinId, boolean value) {
        super(useCaseName, useCaseId, controlJoinId);
        this.value = value;
    }

    public CIPBooleanUseCaseResp(String useCaseName, int useCaseId, int controlJoinId) {
        super(useCaseName, useCaseId, controlJoinId);
    }

    public CIPBooleanUseCaseResp(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId, 0);
    }
}
